package com.djimgou.core.test.initilizer;


import com.djimgou.core.test.proxy.IServiceProxy;
import com.djimgou.core.test.util.FakeBuilder;
import com.djimgou.core.util.EntityRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@Log4j2
@Service
public class GenericDbManager {
    Boolean isInit = false;

    @PersistenceContext
    EntityManager em;

    /*    @Autowired
        Map<String, IServiceProxy> serviceProxyMap;*/
    @Autowired
    EntityRepository entityRepo;

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

    private final Map<Class<? extends Object>, Consumer<? extends Object>> dtoCustomMap = new HashMap();
    private final Map<Class<? extends Object>, Consumer<? extends Object>> dtoCustomPreMap = new HashMap();

    //@PostConstruct
    @Transactional
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

            List<Node> child = fields.stream().filter(field -> map.containsKey(field.getType().getName()) && !Objects.equals(field.getType().getName(), name)).map(field -> {
                map.get(field.getType().getName()).setParent(map.get(name));
                return map.get(field.getType().getName());
            }).collect(Collectors.toList());

            map.get(name).setChildren(child);
        }
        order();
        saveEntities(orderedEntitiesList, Boolean.FALSE);
    }

    public <T extends Object> GenericDbManager potConstructDto(Class<T> dtoClass, Consumer<T> dtoConsumer) {
        dtoCustomMap.put(dtoClass, dtoConsumer);
        return this;
    }

    public <T extends Object> GenericDbManager preConstructDto(Class<T> dtoClass, Consumer<T> dtoConsumer) {
        dtoCustomPreMap.put(dtoClass, dtoConsumer);
        return this;
    }

    public GenericDbManager ignore(Class... entitiesClasses) {
        Arrays.asList(entitiesClasses).forEach(aClass -> {
            this.toIgnore.put(aClass, true);
        });
        return this;
    }

    void initEntities() {
        entityRepo.getEntityMap().keySet().stream().filter(entityType -> !this.toIgnore.containsKey(entityType)).forEach((aClass) -> {
            final Node node = new Node(aClass);
            if (dtoCustomMap.containsKey(node.getDtoClasse())) {
                Consumer<? extends Object> postConstruct = dtoCustomMap.get(node.getDtoClasse());
                node.setPostConstructDto(postConstruct);
            }
            if (dtoCustomPreMap.containsKey(node.getDtoClasse())) {
                Consumer<? extends Object> preConstruct = dtoCustomPreMap.get(node.getDtoClasse());
                node.setPreConstructDto(preConstruct);
            }
            entitiesList.add(node.getClasse());
            map.put(node.getClasse().getName(), node);
        });
    }

    void order() {
        orderedEntitiesList.clear();
        Node.mapTemp.clear();
        map.values().stream().filter(node -> !node.hasChildren()).map(node -> node.getClasse().getName()).forEach(name -> {
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
    public <T> T deepCreate(Class<? extends T> classe) {
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
        return (T) map.get(classe.getName()).getValue();
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
                ReflectionUtils.findFields(dtoClass, f -> !Objects.equals("id", f.getName()) && entityRepo.isManagedEntity(f.getType()), ReflectionUtils.HierarchyTraversalMode.TOP_DOWN).forEach(field -> {
                    final Class<?> type = field.getType();
                    if (finalMapNode.containsKey(type.getName())) {
                        final Node nodeC = finalMapNode.get(type.getName());
                        setField(node.getDto(), field.getName(), nodeC.getValue());
                    }

                });
            }
            if (createFakeDto) {
                node.setDto(FakeBuilder.fake(node.getDto().getClass()));
            }
            if (has(node.getPreConstructDto())) {
                node.getPreConstructDto().accept(node.getDto());
            }
            em.persist(node.getDto());
            final Object value = node.getDto();
            //em.refresh(value);
            System.out.println("Creation en BD de " + value.toString());
            node.setValue(value);
        }
        mapNode = null;
        mapNodeByDto = null;
    }

    public static <T> T get(Class<T> classe) {
        return (T) map.get(classe.getName()).getValue();
    }

}
