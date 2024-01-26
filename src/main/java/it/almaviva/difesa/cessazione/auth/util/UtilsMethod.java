package it.almaviva.difesa.cessazione.auth.util;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.data.common.Sortable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UtilsMethod {

    public static final List<String> ALL_ROLES = List.of(
            Constant.INSTRUCTOR_GRADUATED_ROLE_ID,
            Constant.INSTRUCTOR_OFFICER_ROLE_ID,
            Constant.INSTRUCTOR_SUB_OFFICER_ROLE_ID,
            Constant.APPROVER_GRADUATED_ROLE_ID,
            Constant.APPROVER_OFFICER_ROLE_ID,
            Constant.APPROVER_SUB_OFFICER_ROLE_ID,
            Constant.SIGNATURE_GRADUATED_ROLE_ID,
            Constant.SIGNATURE_OFFICER_ROLE_ID,
            Constant.SIGNATURE_SUB_OFFICER_ROLE_ID,
            Constant.MANAGER_GRADUATED_ROLE_ID,
            Constant.MANAGER_OFFICER_ROLE_ID,
            Constant.MANAGER_SUB_OFFICER_ROLE_ID
    );

    public static final List<String> FORZA_ARMATA_LIST = List.of("EI", "MM", "AM", "CC");

    private UtilsMethod() {
    }

    public static <E extends Sortable> Pageable setSortToPageableIfNecessary(Pageable pageable, Class<?> clazz) {
        if (pageable.getSort().isUnsorted()) {
            Class<E> eClass = (Class<E>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
            E entity = createEntity(eClass);
            Sort sort = entity.getSort() == null ? Sort.unsorted() : entity.getSort();
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        return pageable;
    }

    public static String getClassName(Class<?> clazz) {
        String genericClassTypeName = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
        String[] directoriesAndClass = genericClassTypeName.split("\\.");
        return directoriesAndClass[directoriesAndClass.length - 1].toLowerCase();
    }

    private static <E> E createEntity(Class<E> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public static <T> Page<T> getPageFromList(List<T> list, Pageable pageable) {
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    public static <T> List<T> sortList(List<T> listToSort, Sort.Direction direction, Comparator<T> comparator) {
        if (direction.isAscending()) {
            return listToSort.parallelStream().sorted(comparator).collect(Collectors.toList());
        } else if (direction.isDescending()) {
            return listToSort.parallelStream().sorted(comparator.reversed()).collect(Collectors.toList());
        } else {
            return listToSort;
        }
    }

}