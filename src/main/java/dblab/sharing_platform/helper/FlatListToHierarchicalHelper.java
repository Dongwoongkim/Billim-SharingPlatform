package dblab.sharing_platform.helper;

import dblab.sharing_platform.exception.helper.ConvertException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FlatListToHierarchicalHelper<K, E, D> {

    private List<E> entities;
    private Function<E, D> toDto;
    private Function<E, E> getParent;
    private Function<E, K> getKey;
    private Function<D, List<D>> getChildren;

    public static <K, E, D> FlatListToHierarchicalHelper newInstance(List<E> entities, Function<E, D> toDto,
                                                                     Function<E, E> getParent, Function<E, K> getKey,
                                                                     Function<D, List<D>> getChildren) {
        return new FlatListToHierarchicalHelper<K, E, D>(entities, toDto, getParent, getKey, getChildren);
    }

    public FlatListToHierarchicalHelper(List<E> entities, Function<E, D> toDto, Function<E, E> getParent,
                                        Function<E, K> getKey, Function<D, List<D>> getChildren) {
        this.entities = entities;
        this.toDto = toDto;
        this.getParent = getParent;
        this.getKey = getKey;
        this.getChildren = getChildren;
    }

    public List<D> convert() {
        try {
            return convertInternal();
        } catch (NullPointerException e) {
            throw new ConvertException();
        }
    }

    private List<D> convertInternal() {
        Map<K, D> dtoMap = new HashMap<>();
        List<D> roots = new ArrayList<>();

        for (E e : entities) {
            D dto = toDto(e);
            dtoMap.put(getKey(e), dto);
            if (hasParent(e)) {
                E parent = getParent(e);
                K parentKey = getKey(parent);

                D parentDto = dtoMap.get(parentKey);
                getChildren(parentDto).add(dto);
            } else {
                roots.add(dto);
            }
        }
        return roots;
    }

    public boolean hasParent(E e) {
        return getParent(e) != null;
    }

    public E getParent(E e) {
        return getParent.apply(e);
    }

    public K getKey(E e) {
        return getKey.apply(e);
    }

    public List<D> getChildren(D d) {
        return getChildren.apply(d);
    }

    public D toDto(E e) {
        return toDto.apply(e);
    }
}
