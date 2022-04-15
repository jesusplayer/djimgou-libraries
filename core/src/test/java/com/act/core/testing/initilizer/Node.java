/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.act.core.testing.initilizer;

import com.act.core.model.AbstractBaseEntity;
import com.act.core.model.IEntityDto;
import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.proxy.IServiceProxy;
import lombok.Getter;
import lombok.Setter;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.act.core.util.AppUtils.has;

@Getter
@Setter
public class Node<T extends AbstractBaseEntity, DTO extends IEntityDto> {
    /**
     * dictionaire permettannt de vérifier si un map appartien à orderedEntitiesList
     */
    public static Map<String, String> mapTemp = new HashMap();
    private Node parent;
    private T value;
    private DTO dto;
    private Consumer<DTO> postConstructDto;
    //private DTO dto;
    private Collection<Node> children;
    private final IServiceProxy<T, DTO> service;

    public Node(Node parent, IServiceProxy<T, DTO> service, Consumer<DTO> postConstructDto) {
        // this.buildDto = buildDto;
        this.parent = parent;
        this.service = service;

        Type[] typeArg = ((ParameterizedTypeImpl) service.getClass().getGenericInterfaces()[0])
                .getActualTypeArguments();
        Class<T> tClass = (Class<T>) typeArg[0];
        Class<DTO> dtoClass = (Class<DTO>) typeArg[1];
        this.dto = FakeBuilder.fake(dtoClass);
        this.value = FakeBuilder.fake(tClass);
        if (postConstructDto != null) {
            postConstructDto.accept(this.dto);
        }
        this.children = Collections.EMPTY_LIST;
        this.postConstructDto = postConstructDto;
    }

    public Node(Node parent, IServiceProxy<T, DTO> service) {
        this(parent, service, null);
        this.parent = parent;
    }

    public Node(IServiceProxy<T, DTO> service) {
        this(null, service, null);
    }

    public Node(IServiceProxy<T, DTO> service, Consumer<DTO> postConstructDto) {
        this(null, service, postConstructDto);
    }

    public void setDto(DTO dto) {
        postConstructDto.accept(dto);
        this.dto = dto;

    }

    public void setPostConstructDto(Consumer<DTO> postConstructDto) {
        this.postConstructDto = postConstructDto;
        this.postConstructDto.accept(this.dto);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasParent() {
        return has(parent);
    }

    public static int COUNT = 0;

    public Class<T> getClasse() {
        return (Class<T>) getValue().getClass();
    }

    public Class<DTO> getDtoClasse() {
        return (Class<DTO>) getDto().getClass();
    }

    public void print() {
        Class classe = getClasse();
        final boolean exist = mapTemp.containsKey(classe.getName());
        if (!exist) {
            if (hasChildren()) {
                for (Node child : children) {
                    child.print();
                }
            }
            mapTemp.put(classe.getName(), classe.getName());
            System.out.println(" Noeud" + (++COUNT) + ": " + classe.getName());
            DbManager.orderedEntitiesList.add(DbManager.map.get(classe.getName()));
        }
    }
}
