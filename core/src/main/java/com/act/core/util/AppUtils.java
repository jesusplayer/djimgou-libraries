package com.act.core.util;

import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Service
@Log4j2
public class AppUtils {
    public static UUID fromString(String uuid) {
        return UUID.fromString(uuid);
    }

    public static final List<String> holydays = new ArrayList() {{
        add("25/12");
        add("29/12");
        add("30/12");
        add("31/12");
        add("01/01");
        add("10/02");
        add("11/02");
        add("10/04");
        add("11/04");
        add("01/05");
        add("20/05");
        add("21/05");
        add("31/05");
    }};

    @Autowired
    ResourceLoader resourceLoader;

    public static <T> Double calculEcartype(List<T> list, ToDoubleFunction<T> valueFn, long COUNT) {
        DoubleStream stream = list.stream().mapToDouble(valueFn);
        Double moy = stream.average().orElse(0.0);

        DoubleStream streamEcar = list.stream()
                .mapToDouble(s -> (valueFn.applyAsDouble(s) - moy) * (valueFn.applyAsDouble(s) - moy));

        Double varience = zeroIfNull(streamEcar.sum());

        Double ecartype = Math.sqrt(varience / (COUNT));

        return ecartype;
    }

    public static <T> Double calculEcartype(List<T> list, ToDoubleFunction<T> valueFn) {
        return calculEcartype(list, valueFn, list.size());
    }

    public Date startOfDay(Date date) {
        return startOfTheDay(date);
    }


    /**
     * Permet de faire des itérations à partir des intervalles de date
     *
     * @param startDate Date de début
     * @param endDate   Date de fin
     * @param iterator  Itérateur à appeler
     */
    public static void dateIterate(Date startDate, Date endDate, Consumer<Date> iterator) {
        Date current = startDate;
        while (current.before(endDate) || isDateEquals(current, endDate)) {
            iterator.accept(current);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            calendar.setTime(current);
            calendar.add(Calendar.DATE, 1);
            current = calendar.getTime();
        }
    }

    public static void dateIterateInverse(Date startDate, Date endDate, Consumer<Date> iterator) {
        Date current = endDate;
        while (current.after(startDate) || isDateEquals(current, endDate)) {
            iterator.accept(current);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            calendar.setTime(current);
            calendar.add(Calendar.DATE, -1);
            current = calendar.getTime();
        }
    }

    /**
     * Permet de faire des itérations à partir des intervalles de date
     *
     * @param startDate Date de début
     * @param endDate   Date de fin
     * @param iterator  Itérateur à appeler
     */
    public static void dateIterate(Date startDate, Date endDate, BiConsumer<Date, Integer> iterator) {
        Date current = startDate;
        Integer i = 0;
        while (current.before(endDate) || isDateEquals(current, endDate)) {
            iterator.accept(current, ++i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            calendar.add(Calendar.DATE, 1);
            current = calendar.getTime();
        }
    }

    /**
     * Permet de faire des itérations à partir des intervalles de date
     *
     * @param startDate Date de début
     * @param endDate   Date de fin
     * @param iterator  Itérateur à appeler
     */
    public static void forEachDate(Date startDate, Date endDate, BiConsumer<Date, Integer> iterator) {
        dateIterate(startDate, endDate, iterator);
    }

    /**
     * Permet de faire des itérations à partir des intervalles de date
     *
     * @param startDate Date de début
     * @param endDate   Date de fin
     * @param iterator  Itérateur à appeler
     */
    public static void forEachDate(Date startDate, Date endDate, Consumer<Date> iterator) {
        dateIterate(startDate, endDate, iterator);
    }

    public static Date startOfTheDay(Date date) {
        date = has(date) ? date : Calendar.getInstance().getTime();
        ZoneId zone = ZoneId.systemDefault();
        LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDate();
        return Date.from(localDate.atStartOfDay(zone).toInstant());
    }

    public static Date dayBefore(Date date) {
        return dayBefore(date, 1);
    }

    public static Date dayBefore(Date date, int nbreJour) {
        date = has(date) ? date : Calendar.getInstance().getTime();
        ZoneId zone = ZoneId.systemDefault();
        LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDate();
        return Date.from(localDate.minusDays(nbreJour).atStartOfDay(zone).toInstant());
    }

    public static Date dayAfter(Date date) {
        return dayAfter(date, 1);
    }

    /**
     * Retourne une date qui est le resulat obtenu apères modification de l'heure, minute, seconde miliseconde d'une date
     *
     * @param date         date à utiliser
     * @param heure24      heure
     * @param minute       minute
     * @param seconde      seconde
     * @param milliseconde milliseconde
     * @return la nouvelle date
     */
    public static Date dateUpdate(Date date, int heure24, int minute, int seconde, int milliseconde) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, heure24);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, seconde);
        cal.set(Calendar.MILLISECOND, milliseconde);

        return cal.getTime();
    }

    public static Date dateUpdate(Date date, int heure24, int minute, int seconde) {
        return dateUpdate(date, heure24, minute, seconde, 0);
    }

    /**
     * Retourne le dernier jour ouvrable à partir d'une date donné
     *
     * @param date     date à utiliser
     * @param nbreJour nombre de jour
     * @return nouvelle date
     */
    public static Date lastWorkingDay(Date date, int nbreJour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, -nbreJour);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        while (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {

            cal.add(Calendar.DAY_OF_YEAR, -1);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        }
        return cal.getTime();
    }

    /**
     * Retourne le nbreJour ouvré avant la date
     *
     * @param date     date  à utiliser
     * @param nbreJour nombre de jour
     * @return nouvelle date
     */
    public static Date lastWorkingDayBefore(Date date, int nbreJour) {

        AtomicInteger count = new AtomicInteger(0);
        AtomicReference<Date> aDate = new AtomicReference();
        Date dateDebut = dayBefore(date, nbreJour + 130);
        dateIterateInverse(dateDebut, dayBefore(date), (d) -> {
            if (!isWeekend(d)) {
                int v = count.incrementAndGet();
                if (v == nbreJour) {
                    aDate.set(d);
                }
            }
        });
        return aDate.get();
    }

    /**
     * Retourne la fin du mois correspondant à une date
     *
     * @param date date à utiliser
     * @return nouvelle date
     */
    public static Date endOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int r = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, r);
        return cal.getTime();
    }
    public static Date endOfMonth() {
        return endOfMonth(Calendar.getInstance().getTime());
    }

    /**
     * Retourne le debut du mois correspondant à une date
     *
     * @param date date à utiliser
     * @return la nouvelle date
     */
    public static Date startOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int r = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, r);
        return cal.getTime();
    }

    public static Date startOfMonth() {
        return startOfMonth(Calendar.getInstance().getTime());
    }

    /**
     * Permet de savoir si une date est un weekend
     *
     * @param date date à utiliser
     * @return la nouvelle date
     */
    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    /**
     * Retourne le prochain jour ouvrable à partir d'une date donné
     *
     * @param date     date à utiliser
     * @param nbreJour nombre de jour
     * @return la nouvelle date
     */
    public static Date nextWorkingDay(Date date, int nbreJour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, +nbreJour);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        while (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {

            cal.add(Calendar.DAY_OF_YEAR, 1);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        }
        return cal.getTime();
    }

    /**
     * Ajoute 2 jours ouvrés à la date fournie en paramètre
     *
     * @param date date à traiter
     * @return la nouvelle date
     */
    public static Date datePlus2JoursOuvrees(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);


        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);

        if ((c1.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) || (c1.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY))//jeudi --> lundi ou vendredi --> mardi
        {
            return addDays(date, 4);
        } else {
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)   //samedi --> mardi
            {
                return addDays(date, 3);
            } else {
                return addDays(date, 2);
            }
        }
    }

    /**
     * Ajoute 3 jours ouvrés à la date fournie en paramètre
     *
     * @param date date à utiliser
     * @return nouvelle date
     */
    public static Date datePlus3JoursOuvrees(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);


        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);

        if ((c1.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) || (c1.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) || (c1.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY))
        //mercredi --> lundi, jeudi --> mardi ou vendredi --> mercredi
        {
            return addDays(date, 5);
        } else {
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)   //samedi --> mercredi
            {
                return addDays(date, 4);
            } else {
                return addDays(date, 3);
            }
        }
    }


    /**
     * Ajoute n jour à la date fournie en paramètre
     *
     * @param date date à utiliser
     * @param days nombre de jour
     * @return nouvelle date
     */

    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * Verifie si l'heure d'une date correspond à une ou plusieur heures données
     *
     * @param date  date à utiliser
     * @param hours heure à utiliser
     * @return un boolean qui représente le resultat du test
     */
    public static boolean containsHour(Date date, List<String> hours) {
        return hours.stream().anyMatch(h -> containsHour(date, h));
    }

    /**
     * Verifie si l'heure d'une date correspond à une heure donnée
     *
     * @param date    date à utiliser
     * @param hourMin Heure à verifier sous le format 'HH:mm' 15:20
     * @return un boolean qui représente le resultat du test
     */
    public static boolean containsHour(Date date, String hourMin) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String r = sdf.format(date);
//        String r2 = sdf.format(date);
        return r.equals(hourMin);
    }

    /*public static boolean containsHourWithAprox(Date date, String hourMin) {
        SimpleDateFormat hf = new SimpleDateFormat("HH");
        SimpleDateFormat mf = new SimpleDateFormat("mm");
        String r = hf.format(date);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,);
        String r2 = sdf.format(date);
        return r.equals(hourMin);
    }*/
    public boolean isHolidays(Date date) {
        return holydays.stream().anyMatch(str -> isDayMonthOfDate(date, str));
    }

    public static Date lastWorkingDay(Date date) {
        return lastWorkingDay(date, 1);
    }

    public static Date dayAfter(Date date, int nbreJour) {
        date = has(date) ? date : Calendar.getInstance().getTime();
        ZoneId zone = ZoneId.systemDefault();
        LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDate();
        return Date.from(localDate.plusDays(nbreJour).atStartOfDay(zone).toInstant());
    }

    public static Date today() {
        return Calendar.getInstance().getTime();
    }

    public String now() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date now = Calendar.getInstance().getTime();
        return sdf.format(now);
    }

    public Date endOfDay(Date date) {
        return endOfTheDay(date);
    }

    public static Date endOfTheDay(Date date) {
        date = has(date) ? date : Calendar.getInstance().getTime();
        ZoneId zone = ZoneId.systemDefault();
        LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(zone).toLocalDate();
        LocalDateTime endOfDate = LocalTime.MAX.atDate(localDate);
        return Date.from(endOfDate.atZone(zone).toInstant());
    }

    public static int dayOfMonth(Date date) {
        LocalDate lStartDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return lStartDate.getDayOfMonth();
    }

    public static int yearDiff(Date startDate, Date endDate) {
        LocalDate lStartDate = startDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate lEndDate = endDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        Period age = Period.between(lStartDate, lEndDate);
        return age.getYears();
    }

    /**
     * Différence entre une date de début et une date de fin
     * Si la date de debut
     *
     * @param startDate date de début
     * @param endDate   date de fin
     * @return la différence de jour
     */
    /*public static long dayDiff(Date startDate, Date endDate) {

        LocalDate lStartDate = LocalDate.now();
        if (has(startDate)) {
            lStartDate = startDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        LocalDate lEndDate = LocalDate.now();

        if (has(endDate)) {
            lEndDate = endDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        long noOfDaysBetween = java.time.temporal.ChronoUnit.DAYS.between(lStartDate, lEndDate);
        return noOfDaysBetween;
    }*/
   /* public static long hourDiff(Date startDate, Date endDate) {

        LocalDate lStartDate = LocalDate.now();
        if (has(startDate)) {
            lStartDate = startDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        LocalDate lEndDate = LocalDate.now();

        if (has(endDate)) {
            lEndDate = endDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        long noOfDaysBetween = ChronoUnit.HOURS.between(lStartDate, lEndDate);
        return noOfDaysBetween;
    } */
    public static int hourDiff(Date startDate, Date endDate) {
        /*  * secs = secs % 3600;
         * int mins = secs / 60;
         * secs = secs % 60;*/
       /* DateTime start = new DateTime(startDate), end = new DateTime(endDate);
        org.joda.time.Period p = new org.joda.time.Period(start, end);
        return p.getHours();*/
        long secs = (endDate.getTime() - startDate.getTime()) / 1000;
        int hours = (int) (secs / 3600);
        return hours;

    }

    public static int dayDiff(Date startDate, Date endDate) {
        DateTime start = new DateTime(startDate), end = new DateTime(endDate);
        org.joda.time.Period p = new org.joda.time.Period(start, end);
        return p.getDays();
    }

    /**
     * Calcule le nombre de jour dans une année donnée
     *
     * @param curentDate date à utiliser
     * @return nombre de jour
     */
    public long daysInYear(Date curentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curentDate);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date start = cal.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(curentDate);
        cal2.set(Calendar.DAY_OF_MONTH, 31); // new years eve
        cal2.set(Calendar.MONTH, 11); // 11 = december
        Date end = cal2.getTime();

        return dayDiff(start, end);
    }

    public static Object getField(String name, Object o) {
        for (Method m : o.getClass().getMethods()) {
            if (m.getName().startsWith("get") && (name.length() + 3) == m.getName().length()) {
                if (m.getName().toLowerCase().endsWith(name.toLowerCase())) {
                    try {
                        return m.invoke(o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static List<Class> getClasses(String packageName, Class annotation) {
        return getClasses(packageName, annotation, a -> true);
    }

    /**
     * Scanne les entités qui contienne la l'annotation annotation. Elle utilise skip pour
     * préciser les classes à exclure
     *
     * @param packageName Package à scanner
     * @param annotation  annotation que doivent contenir les classes extraites
     * @param skip        Classe à exclure
     * @return liste des classes
     */
    public static List<Class> getClasses(String packageName, Class annotation, Class[] skip) {
        return getClasses(packageName, annotation, c -> {
            List<Class> clas = Arrays.asList(skip);
            return !clas.contains(c);
        });
    }

    public static List<Class> getClasses(String packageName, Class annotation, Predicate<Class> filterFn) {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);

        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

        final Set<BeanDefinition> beans = provider.findCandidateComponents(packageName);

        return beans.stream()
                .filter((BeanDefinition bean) -> {
                    try {
                        Class clazz = Class.forName(bean.getBeanClassName());
                        Annotation[] an = clazz.getDeclaredAnnotationsByType(annotation);
                        boolean cond1 = !(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || an.length == 0);
                        boolean cond2 = filterFn.test(clazz);
                        return cond1 && cond2;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).map((BeanDefinition bean) -> {
                    try {
                        return Class.forName(bean.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }


    /**
     * Scanne les entités dans le package
     *
     * @param packageName nom du package sous forme : com.carrent.entites
     * @param filterFn    filtre
     * @return la liste des classes
     */
    public static List<Class> getClasses(String packageName, Predicate<Class> filterFn) {
        return getClasses(packageName, Entity.class, filterFn);
    }

    /**
     * Scanne les entités dans le package
     *
     * @param packageName nom du package sous forme : com.carrent.entites
     * @return liste des classes
     */
    public static List<Class> getClasses(String packageName) {
        return getClasses(packageName, e -> true);
    }

    /**
     * recup
     *
     * @param packageName nom du package sous forme : com.carrent.entites
     * @param skip        classe à exclure
     * @return la liste des classes
     */
    public static List<Class> getClasses(String packageName, Class[] skip) {
        return getClasses(packageName, c -> {
            List<Class> clas = Arrays.asList(skip);
            return !clas.contains(c);
        });
    }

    /**
     * retourne les classe sous forme de Set
     *
     * @param packageName nom du package de la forme com.company.package
     * @return la liste des classes
     */
    public Set<Class> getClassesSet(String packageName) {
        return getClasses(packageName, e -> true).stream().collect(Collectors.toSet());
    }

    public Set<Class> getClassesSet(String packageName, Predicate<Class> filterFn) {
        return getClasses(packageName, filterFn).stream().collect(Collectors.toSet());
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private List<Class> findClasses(File directory, String packageName) {
        List classes = new ArrayList(1);
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

    /**
     * Fait la somme de deux double sans causer de NulpointerException
     *
     * @param value1 nombre à additioner
     * @param value2 nombre à additioner
     * @return le resultat de l'addition
     */
    public static Double addDouble(Double value1, Double value2) {
        return zeroIfNull(value1) + zeroIfNull(value2);
    }

    /**
     * Retourne 0.0 si la variable est nulle sinon retourne la variable
     *
     * @param value1 le nombre à traiter
     * @return retourne le nombre sans null
     */
    public static Double zeroIfNull(Double value1) {
        return Optional.ofNullable(value1).orElse(0.0);
    }

    public static Integer zeroIfNull(Integer value1) {
        return Optional.ofNullable(value1).orElse(0);
    }

    public static Double addDouble(Double... values) {
        Double acc = 0.0;
        for (Double value : values) {
            acc += zeroIfNull(value);
        }
        return acc;
    }

    /**
     * @param number le nombre à négativiser
     * @return la negation
     */
    public Double negate(Double number) {
        return -zeroIfNull(number);
    }

    /**
     * Fait la somme de deux entier sans causer de NulpointerException
     *
     * @param value1 valeur à additionner
     * @param value2 valeur à additioner
     * @return la somme des deux valeurs
     */
    public static Integer addInteger(Integer value1, Integer value2) {
        return zeroIfNull(value1) + zeroIfNull(value2);
    }

    /**
     * Multiplie deux double sans causer de NulpointerException
     *
     * @param value1 valeur à multiplier
     * @param value2 valeur à multiplier
     * @return un double qui represente le résultat de la miltiplication
     */
    public static Double multiplyDouble(Double value1, Double value2) {
        return zeroIfNull(value1) * zeroIfNull(value2);
    }

    public static Double divideDouble(Double value1, Double value2) {
        final Double aDouble = zeroIfNull(value2);
        return aDouble == 0.0 ? 0.0 : (zeroIfNull(value1) / aDouble);
    }

    public static Double multiplyDouble(Double... values) {
        Double acc = Double.valueOf("1");
        for (Double value : values) {
            acc *= zeroIfNull(value);
        }
        return acc;
    }

    public static Integer multiplyInteger(Integer... values) {
        Integer acc = 1;
        for (Integer value : values) {
            acc *= zeroIfNull(value);
        }
        return acc;
    }

    /**
     * retourne l'arrondi du nombre à n positions
     *
     * @param number   nombre à arrondir
     * @param position nombre de chiffres après la virgule
     * @return un double qui represente l'arrondi obtenu
     */
    public static Double arrondi(double number, int position) {
        return round(number, position);
    }

    public static double min(double... values) {
        OptionalDouble opt = DoubleStream.of(values).min();
        return opt.orElse(0.0);
    }

    public static double minNotNull(double... values) {
        OptionalDouble opt = DoubleStream.of(values).filter(a -> a != 0.0)
                .min();
        return opt.orElse(0.0);
    }

    public static double max(double... values) {
        OptionalDouble opt = DoubleStream.of(values).max();
        return opt.orElse(0.0);
    }

    public static Double minDouble(Double val1, Double val2) {
        Double min = val1;
        if (val1 != null && val2 != null) {
            return Math.min(val1, val2);
        }
        if (min == null) {
            min = val2;
        }
        return zeroIfNull(min);
    }

    /**
     * retourne le minimum de val1*coef1 et val2*coef2
     *
     * @param val1  valeur 1
     * @param coef1 coeficient 1
     * @param val2  valeur 2
     * @param coef2 coeficient 1
     * @return le resultat
     */
    public static Double minFoisCoefficientDouble(Double val1, Double coef1,
                                                  Double val2, Double coef2) {

        Double prod1 = zeroIfNull(val1) * zeroIfNull(coef1);
        Double prod2 = zeroIfNull(val2) * zeroIfNull(coef2);

        if (prod1 == 0.0) {
            return prod2;
        }

        if (prod2 == 0.0) {
            return prod1;
        }

        return Math.min(prod1, prod2);
    }


    /**
     * retourne l'arrondi du nombre à n positions
     *
     * @param number   nombre à utiliser
     * @param position nombre de chiffres après la virgule
     * @return un Double qui représente l'arrondi obtenu
     */
    public static Double round(double number, int position) {
        double res = IntStream.range(1, position + 1).reduce(1, (old, n) -> old * 10);
        return Math.round(number * res) / res;
    }

    public static Object numberFormat(Object amount) {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.FRANCE);

        if (amount instanceof Number) {
            return numberFormatter.format(amount);
        }

        return amount;
        /*System.out.println(quantityOut + "   " + currentLocale.toString());
        System.out.println(amountOut + "   " + currentLocale.toString());
        Double currencyAmount = new Double(9876543.21);
        Currency currentCurrency = Currency.getInstance(currentLocale);
        NumberFormat currencyFormatter =
                NumberFormat.getCurrencyInstance(currentLocale);

        System.out.println(
                currentLocale.getDisplayName() + ", " +
                        currentCurrency.getDisplayName() + ": " +
                        currencyFormatter.format(currencyAmount));*/

    }

    public static Object numberFormat(Object amount, int minFractionDigit) {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.FRANCE);
        numberFormatter.setMinimumFractionDigits(minFractionDigit);
        if (amount instanceof Number) {
            return numberFormatter.format(amount);
        }
        return amount;

    }

    public boolean dateEquals(Date date1, Date date2) {
        return isDateEquals(date1, date2);
    }

    public static boolean isDateEquals(Date date1, Date date2) {
        String pattern = "MM-dd-yyyy";
        return isDateEquals(date1, date2, pattern);
    }

    /**
     * Vérifie si deux dates sont identiques selon un format précis:
     * MM-dd-yyyy , MM/dd/yyyy, MM/dd, etc...
     *
     * @param date1  première date à tester
     * @param date2  deuxième date à tester
     * @param format format de test
     * @return un boolean qui représente le resultat du test
     */
    public static boolean isDateEquals(Date date1, Date date2, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String d1 = simpleDateFormat.format(date1);
        String d2 = simpleDateFormat.format(date2);
        return d1.equals(d2);
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * Vérifie si une date contient un jour/mois passé en paramètre
     *
     * @param date     date
     * @param dayMonth jour/mois. Ex: 12/01, 02/11, 03/09
     * @return un boolean qui représente le resultat du test
     */
    public static boolean isDayMonthOfDate(Date date, String dayMonth) {
        String pattern = "dd/MM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String d1 = simpleDateFormat.format(date);
        return d1.equals(dayMonth);
    }


    public String[] getRandomPassword() {
        int len = 10;
        int randNumOrigin = 10, randNumBound = 122;
        SecureRandom random = new SecureRandom();
        String pass = random.ints(randNumOrigin, randNumBound + 1)
                .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i) || isSpecialChar(i))
                .limit(len)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();

        return new String[]{pass.substring(0, 5), pass.substring(5)};
    }

    public boolean isSpecialChar(int i) {
        String v = String.valueOf((char) i);
        return "*".equals(v) || "@".equals(v) || "#".equals(v) || "!".equals(v);
    }

    public static boolean has(Object... objects) {
        return objects != null && objects.length > 0;
    }

    public static boolean has(Object object) {
        return object != null;
    }

    public static boolean has(String object) {
        return object != null && !object.isEmpty();
    }

    public static boolean has(List<?> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * Retourne vrai si tous les objets passé en paramètre ne sont pas null
     *
     * @param objects objets à tester
     * @return un boolean qui représente le resultat du test
     */
    public static boolean isNotNull(Object... objects) {
        if (has(objects)) {
            for (Object o : objects) {
                if (!has(o)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Verifier si un Double est vide c'est à dire null, NaN  ou zero
     *
     * @param number nombre
     * @return true si la valeur est vide
     */
    public static boolean isEmptyDouble(Double number) {
        return !has(number) || number.isNaN() || number == 0.0;
    }

    /**
     * Converti les classe de' l'app en donnant le bon nom
     *
     * @param className nom de la classe
     * @return le nom en francais
     */
    public static String localizeClassName(String className) {
        return className.replaceAll("Authority", "Role");
    }

    public static <T> List<T> reverse(List<T> list) {
        List<T> newItems = new ArrayList<>(1);
        for (int i = list.size() - 1; i >= 0; i--) {
            newItems.add(list.get(i));
        }
        return newItems;
    }
    public static String fileExt(String fileName){
        String fName = fileName;
        return Optional.ofNullable(fName)
                .filter(s -> s.contains("."))
                .map(s -> s.substring(fName.lastIndexOf(".")+1)).orElse("");

    }
    /**
     * Execute la fonction maxTries  de fois tantqu'il ya une exception
     *
     * @param fn       fonction à appeler
     * @param maxTries nombre de tentatives max
     * @param <T>      parametre
     * @return l'entite retournée
     */
    public static <T> T retryOnException(Supplier<T> fn, int maxTries) {
        int count = 0;
        T result;
        while (true) try {
            // Some Code
            // break out of loop, or return, on success
            result = fn.get();
            break;
        } catch (Exception e) {
            // handle exception
            if (++count > maxTries) throw e;
            log.warn("Relance après une exception. Nombre de relance (" + count + ")");
        }
        return result;
    }

    /**
     * Execute la fonction maxTries  de fois tantqu'il ya une exception
     * L'idée par exemple est de faire une requete blp et a chaque fois qu'une exception survient
     * on appelle la methode isOnline qui va chercher le serveur disponible
     *
     * @param fn               fonction à appeler
     * @param afterExceptionFn fonction à appeler àprès cahque exception
     * @param maxTries         nombre de tentatives max
     * @param <T>              parametre
     * @return l'entite retournée
     */
    public static <T> T retryOnExcept(Supplier<T> fn, Supplier afterExceptionFn, int maxTries) throws Exception {
        int count = 0;
        Exception exp = null;
        T result;
        while (true) try {
            // Some Code
            // break out of loop, or return, on success
            result = fn.get();
            break;
        } catch (Exception e) {
            // handle exception
            if (++count > maxTries) exp = e;
            afterExceptionFn.get(); // après une exception on appele une fonction spécifique
            log.warn("Relance après une exception. Nombre de relance (" + count + ")");
        } finally {
            if (exp != null) {
                throw exp;
            }
        }

        return result;
    }

    public static <T> Page<T> toPage(Page<T> page, List<T> newContent, LongSupplier sizeSuplier) {
        return PageableExecutionUtils.getPage(
                newContent,
                page.getPageable(),
                sizeSuplier);
    }

    public static <T> Page<T> toPage(List<T> newContent) {
        return new PageImpl<>(newContent);
    }

    public static <T> Page<T> toPage(Pageable pageable, List<T> newContent, int totalAmount) {
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), totalAmount);
        final Page<T> page = new PageImpl<>(newContent.subList(start, end), pageable, totalAmount);
        return page;
    }
}
