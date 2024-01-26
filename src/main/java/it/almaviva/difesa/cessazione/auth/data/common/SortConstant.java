package it.almaviva.difesa.cessazione.auth.data.common;

import org.springframework.data.domain.Sort;

public class SortConstant {

    private SortConstant() {
    }

    public static final Sort UNSORTED = Sort.unsorted();
    public static final Sort SORT_BY_ID = Sort.by("id");
    public static final Sort SORT_BY_DESCRIPTION = Sort.by("description");
    public static final Sort SORT_BY_FISCAL_CODE = Sort.by("fiscalCode");
    public static final Sort SORT_BY_LAST_UPDATED_DATE = Sort.by("lastUpdatedDate");
    public static final Sort SORT_BY_NAME = Sort.by("name");
    public static final Sort SORT_BY_PRIVILEGE_CODE = Sort.by("privilegeCode");
    public static final Sort SORT_BY_PROCEDURE_START_DATE = Sort.by("procedureStartDate");
    public static final Sort SORT_BY_START_DATE = Sort.by("startDate");
    public static final Sort SORT_BY_END_DATE = Sort.by("endDate");
    public static final Sort SORT_BY_USER_FISCAL_CODE = Sort.by("userFiscalCode");
    public static final Sort SORT_BY_USER_NAME = Sort.by("userName");

}
