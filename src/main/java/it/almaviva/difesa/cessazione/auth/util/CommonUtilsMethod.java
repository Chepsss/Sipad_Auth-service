package it.almaviva.difesa.cessazione.auth.util;


import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.Period;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
public class CommonUtilsMethod {


    private CommonUtilsMethod() {}

    public static boolean isNotBlankString(String string) {
        return string != null && !string.trim().isEmpty();
    }

    public static <T> boolean isNotCollectionEmpty(Collection<T> collection) {
        return !CollectionUtils.isEmpty(collection);
    }

    public static String getClassName(Class<?> clazz) {
        String genericClassTypeName = clazz.getTypeName();
        String[] directoriesAndClass = genericClassTypeName.split("\\.");
        return directoriesAndClass[directoriesAndClass.length - 1].toLowerCase();
    }

    public static boolean isValidDateFormat(String dateStr) {
        DateFormat sdf = new SimpleDateFormat(Constant.YYYY_MM_DD);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            log.warn(e.getMessage());
            return false;
        }

        return true;
    }

    public static boolean arePeriodOverlapped(Period period1, Period period2) {
        return period1.equals(period2) ||
                period1.getStartDate().equals(period2.getEndDate()) ||
                period1.getEndDate().equals(period2.getStartDate()) ||
                (period1.getStartDate().isBefore(period2.getEndDate()) && period1.getEndDate().isAfter(period2.getStartDate()));
    }

    public static LocalDate dateToLocalDate(Date date) {
        if (date == null)
            return null;
        else
            return date.toLocalDate();
    }
}