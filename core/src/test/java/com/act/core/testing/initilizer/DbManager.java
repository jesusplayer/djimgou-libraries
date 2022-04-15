package com.act.core.testing.initilizer;

import com.act.core.model.AbstractBaseEntity;
import com.act.core.model.BaseBdEntity;
import com.act.core.model.IEntityDto;
import com.act.core.testing.app.EntityDtoConsumer;
import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.proxy.IServiceProxy;
import com.act.core.util.AppUtils;
import lombok.extern.log4j.Log4j2;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.act.core.util.AppUtils.has;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@Log4j2
@Service
public class DbManager {
    Boolean isInit = false;

    @Autowired
    Map<String, IServiceProxy> serviceProxyMap;

    Map<Class<? extends IServiceProxy>, Boolean> toIgnore = new HashMap<>();

    /**
     * Map contenant les noeuds hiérarchiques
     */
    public static Map<String, Node> map = new HashMap();

    /**
     * Liste des objets instanciés
     */
    public static Collection<Class> entitiesList = new ArrayList<>();
    /**
     * ENtités classsé par ordre hierarchique
     */
    public static List<Node> orderedEntitiesList = new ArrayList<>();

    private final Map<Class<? extends IEntityDto>, EntityDtoConsumer<? extends IEntityDto>> dtoCustomMap = new HashMap();

    //@PostConstruct
    public void initDb() {
        if (!isInit) {
            map.clear();
            this.launchInit();
        }
        isInit = true;
    }

    private void launchInit() {
        initEntities();
        // recherche des fils et des pères
        for (Class aClass : entitiesList) {
            Field[] declaredFields = aClass.getDeclaredFields();
            final String name = aClass.getName();
            //final List<Field> fields = Arrays.asList(declaredFields);
            final List<Field> fields = ReflectionUtils.findFields(aClass, field -> true, ReflectionUtils.HierarchyTraversalMode.TOP_DOWN);

            List<Node> child = fields.stream().filter(field ->
                    map.containsKey(field.getType().getName())
                            && !Objects.equals(field.getType().getName(), name)
            ).map(field -> {
                map.get(field.getType().getName()).setParent(map.get(name));
                return map.get(field.getType().getName());
            }).collect(Collectors.toList());

            map.get(name).setChildren(child);
        }
        order();
        saveEntities(orderedEntitiesList, Boolean.FALSE);
    }

    public DbManager ignore(Class<? extends IServiceProxy>... serviceProxyClasses) {
        Arrays.asList(serviceProxyClasses).forEach(aClass -> {
            this.toIgnore.put(aClass, true);
        });
        return this;
    }

    public <T extends IEntityDto> DbManager potConstructDto(Class<T> dtoClass, EntityDtoConsumer<T> dtoConsumer) {
        dtoCustomMap.put(dtoClass, dtoConsumer);
        return this;
    }


    void initEntities() {
        serviceProxyMap.values().stream()
                .filter(iServiceProxy -> !this.toIgnore.containsKey(iServiceProxy.getClass()))
                .forEach((iServiceProxy) -> {
                    final Node node = new Node<>(iServiceProxy);
                    if (dtoCustomMap.containsKey(node.getDtoClasse())) {
                        EntityDtoConsumer<? extends IEntityDto> postConstruct = dtoCustomMap.get(node.getDtoClasse());
                        node.setPostConstructDto(postConstruct);
                    }
                    entitiesList.add(node.getClasse());
                    map.put(node.getClasse().getName(), node);
                });
    }

    void order() {
        orderedEntitiesList.clear();
        Node.mapTemp.clear();
        map.values().stream().filter(node -> !node.hasChildren()
        ).map(node -> node.getClasse().getName()).forEach(name -> {
            map.get(name).print();
            Node.mapTemp.put(name, name);
        });

        map.values().stream().filter(Node::hasChildren).forEach(Node::print);

    }

    /**
     * Create new Entity in database with his dependency
     *
     * @param classe
     * @return
     */
    public AbstractBaseEntity deepCreate(Class<? extends BaseBdEntity> classe) {
        final int size = orderedEntitiesList.size();
        for (int i = 0; size > i; i++) {
            Node node = orderedEntitiesList.get(i);
            if (Objects.equals(node.getClasse(), classe)) {
                List<Node> listToSave = orderedEntitiesList.stream().skip(i).collect(Collectors.toList());
                if (!listToSave.isEmpty()) {
                    saveEntities(listToSave, true);
                }
                break;
            }
        }
        return map.get(classe.getName()).getValue();
    }

    //@Commit

    /**
     * Enregistre la hierarchie d'une entité dans la BD
     *
     * @param orderedEntities
     * @param createFakeDto
     */
    void saveEntities(List<Node> orderedEntities, Boolean createFakeDto) {
        Map<String, Node> mapNode = new HashMap<>();
        Map<String, Node> mapNodeByDto = new HashMap<>();
        for (Node node : orderedEntities) {
            final Class classe = node.getClasse();
            final Class dtoClass = node.getDto().getClass();

            mapNode.put(classe.getName(), node);
            mapNodeByDto.put(dtoClass.getName(), node);
            if (node.hasChildren()) {
                Map<String, Node> finalMapNode = mapNode;
                Map<String, Node> finalMapNodeByDto = mapNodeByDto;
                ReflectionUtils.findFields(
                        dtoClass,
                        f -> Objects.equals(f.getType().getName(), UUID.class.getName())
                                && !Objects.equals("id", f.getName())
                                && f.getName().endsWith("Id"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN
                ).forEach(field -> updateChildFields(node, finalMapNode, finalMapNodeByDto, field));
            }
            if (createFakeDto) {
                node.setDto(FakeBuilder.fake(node.getDto().getClass()));
            }
            final Object value = node.getService().create(node.getDto());
            System.out.println("Creation en BD de " + value.toString());
            node.setValue((AbstractBaseEntity) value);
        }

        mapNode = null;
        mapNodeByDto = null;
    }

    void updateChildFields(Node node, Map<String, Node> finalMapNode, Map<String, Node> finalMapNodeByDto, Field field) {
        final Class dtoClass = node.getDto().getClass();
        final Node childDtoNode = finalMapNodeByDto.get(dtoClass.getName());
        // on vérifie si ca existe avec le suffixe Ob.
        // en enlevant les deux dernieres lettres
        String objName = field.getName() + "Ob";
        List<Field> fields = ReflectionUtils.findFields(childDtoNode.getClasse(), field1 ->
                        Objects.equals(field1.getName(), objName),
                ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP
        );
        if (!AppUtils.has(fields)) {
            String objName2 = field.getName().substring(0, field.getName().length() - 2);
            fields = ReflectionUtils.findFields(childDtoNode.getClasse(), field1 ->
                            Objects.equals(field1.getName(), objName2),
                    ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP
            );
        }
        if (AppUtils.has(fields)) {
            Field fieldOb = fields.get(0);
            final Node nodeC = finalMapNode.get(fieldOb.getType().getName());
            setField(node.getDto(), field.getName(), nodeC.getValue().getId());
        }
    }

    public static <T extends AbstractBaseEntity> T get(Class classe) {
        return (T) map.get(classe.getName()).getValue();
    }

}
