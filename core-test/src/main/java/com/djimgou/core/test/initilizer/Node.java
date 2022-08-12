/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.djimgou.core.test.initilizer;

import com.djimgou.core.test.util.FakeBuilder;
import com.djimgou.core.util.AppUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
public class Node<T> {
    /**
     * dictionaire permettannt de vérifier si un map appartien à orderedEntitiesList
     */
    public static Map<String, String> mapTemp = new HashMap();
    private Node parent;
    private T value;
    private T dto;
    private Consumer<T> postConstructDto;
    private Consumer<T> preConstructDto;
    //private DTO dto;
    private Collection<Node> children;

    public Node(Node parent, Class<T> entityClass, Consumer<T> postConstructDto) {
        // this.buildDto = buildDto;
        this.parent = parent;

        this.dto = FakeBuilder.fake(entityClass);
        this.value = FakeBuilder.fake(entityClass);
        if (postConstructDto != null) {
            postConstructDto.accept(this.dto);
        }
        this.children = Collections.EMPTY_LIST;
        this.postConstructDto = postConstructDto;
    }

    public Node(Node parent, Class<T> entityClass, Consumer<T> postConstructDto, Consumer<T> preConstructDto) {
        // this.buildDto = buildDto;
        this(parent, entityClass, postConstructDto);
        this.preConstructDto = preConstructDto;
    }

    public Node(Node parent, Class<T> entityClass) {
        this(parent, entityClass, null);
        this.parent = parent;
    }

    public Node(Class<T> entityClass) {
        this(null, entityClass, null);
    }

    public Node(Class<T> entityClass, Consumer<T> postConstructDto) {
        this(null, entityClass, postConstructDto);
    }

    public void setDto(T dto) {
        postConstructDto.accept(dto);
        this.dto = dto;

    }

    public void setPostConstructDto(Consumer<T> postConstructDto) {
        this.postConstructDto = postConstructDto;
        this.postConstructDto.accept(this.dto);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasParent() {
        return AppUtils.has(parent);
    }

    public static int COUNT = 0;

    public Class<T> getClasse() {
        return (Class<T>) getValue().getClass();
    }

    public Class<T> getDtoClasse() {
        return (Class<T>) getDto().getClass();
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
            GenericDbManager.orderedEntitiesList.add(GenericDbManager.map.get(classe.getName()));
        }
    }
}
