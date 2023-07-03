package dblab.sharing_flatform.helper;

import dblab.sharing_flatform.exception.category.CantConvertException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FlatListToHierarchicalHelper <K,E,D> {

    private List<E> entities;
    private Function<E, D> toDto;
    private Function<E, E> getParent;
    private Function<E, K> getKey;
    private Function<D, List<D>> getChildren;

    public static <K, E, D> FlatListToHierarchicalHelper newInstance(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
        return new FlatListToHierarchicalHelper<K, E, D>(entities, toDto, getParent, getKey, getChildren);
    }

    public FlatListToHierarchicalHelper(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren) {
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
            throw new CantConvertException();
        }
    }

    private List<D> convertInternal() {
        Map<K, D> dtoMap = new HashMap<>();
        List<D> roots = new ArrayList<>();

        for (E e : entities) {
            D dto = toDto(e); // entity -> dto
            dtoMap.put(getKey(e), dto); // dto를 map에 put
            if (hasParent(e)) { // 부모 엔티티 있는지 검사, 부모 엔티티 있으면
                E parent = getParent(e);
                K parentKey = getKey(parent); //  부모 Id 추출

                D parentDto = dtoMap.get(parentKey); // 부모 엔티티를 dtoMap에서 꺼내 (findAllOrderByParentIdAscNullsFirstCategoryIdAsc() 할 예정이므로 반드시 있음)
                getChildren(parentDto).add(dto); // parentDto의 자식 리스트에 dto 추가
            } else {
                roots.add(dto); // 부모 엔티티 없으면 roots에 dto add
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
