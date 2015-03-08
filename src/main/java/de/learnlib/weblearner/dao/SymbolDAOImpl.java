package de.learnlib.weblearner.dao;

import de.learnlib.weblearner.entities.IdRevisionPair;
import de.learnlib.weblearner.entities.Project;
import de.learnlib.weblearner.entities.Symbol;
import de.learnlib.weblearner.entities.SymbolGroup;
import de.learnlib.weblearner.entities.SymbolVisibilityLevel;
import de.learnlib.weblearner.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import javax.validation.ValidationException;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of a SymbolDAO using Hibernate.
 */
public class SymbolDAOImpl implements SymbolDAO {

    private final SymbolGroupDAO symbolGroupDAO;

    public SymbolDAOImpl(SymbolGroupDAO symbolGroupDAO) {
        this.symbolGroupDAO = symbolGroupDAO;
    }

    @Override
    public void create(Symbol symbol) throws ValidationException {
        // start session
        Session session = HibernateUtil.getSession();
        HibernateUtil.beginTransaction();
        try {
            // create the symbol
            create(session, symbol);

            System.out.println("----------------");
            for (int i = 0; i < symbol.getActions().size(); i++) {
                System.out.println(symbol.getActions().get(i));
            }
            System.out.println("----------------");

            HibernateUtil.commitTransaction();

        // error handling
        } catch (javax.validation.ConstraintViolationException
                 | org.hibernate.exception.ConstraintViolationException e) {
            HibernateUtil.rollbackTransaction();
            symbol.setId(0L);
            symbol.setRevision(0L);
            throw new ValidationException("Could not create symbol because it was invalid.", e);
        }
    }

    @Override
    public void create(List<Symbol> symbols) throws ValidationException {
        // start session
        Session session = HibernateUtil.getSession();
        HibernateUtil.beginTransaction();
        try {
            // create the symbol
            for (Symbol symbol : symbols) {
                create(session, symbol);
            }
            HibernateUtil.commitTransaction();

            // error handling
        } catch (javax.validation.ConstraintViolationException
                | org.hibernate.exception.ConstraintViolationException e) {
            HibernateUtil.rollbackTransaction();
            for (Symbol symbol : symbols) {
                symbol.setId(null);
                symbol.setRevision(null);
            }
            throw new ValidationException("Could not create symbol because it was invalid.", e);
        }
    }

    private void create(Session session, Symbol symbol) {
        // new symbols should have a project, not an id and not a revision
        if (symbol.getProject() == null || symbol.getId() != null || symbol.getRevision() != null) {
            throw new ValidationException(
                    "To create a symbols it must have a Project but not haven an ID or and revision");
        }

        Project project = (Project) session.load(Project.class, symbol.getProjectId());

        // test for unique constrains
        checkUniqueConstrains(session, symbol); // will throw exception if the symbol is invalid

        // get the current highest symbol id in the project and add 1 for the next id
        long id = project.getNextSymbolId();
        project.setNextSymbolId(id + 1);
        session.update(project);

        // set id, project id and revision and save the symbol
        symbol.setId(id);
        symbol.setRevision(1L);
        project.addSymbol(symbol);

        SymbolGroup group = (SymbolGroup) session.byNaturalId(SymbolGroup.class)
                                                    .using("project", project)
                                                    .using("id", symbol.getGroupId())
                                                    .load();
        if (group == null) {
            group = project.getDefaultGroup();
        }
        group.addSymbol(symbol);

        if (symbol.getActions() != null) {
            for (int i = 0; i < symbol.getActions().size(); i++) {
                symbol.getActions().get(0).setNumber(i);
            }
        }

        symbol.beforeSave();
        session.save(symbol);
    }

    @Override
    public List<Symbol> getAll(Long projectId, List<IdRevisionPair> idRevPairs) {
        // no DB interaction if no symbols are requested
        if (idRevPairs.isEmpty()) {
            return new LinkedList<>();
        }

        // prepare the subquerys for the id/ revision pairs
        Disjunction symbolIdRestrictions = Restrictions.disjunction();
        for (IdRevisionPair pair : idRevPairs) {
            symbolIdRestrictions.add(Restrictions.and(
                    Restrictions.eq("id", pair.getId()),
                    Restrictions.eq("revision", pair.getRevision())
            ));
        }
        DetachedCriteria symbolIds = DetachedCriteria.forClass(Symbol.class)
                                                        .add(Restrictions.eq("project.id", projectId))
                                                        .add(symbolIdRestrictions)
                                                        .setProjection(Projections.property("symbolId"));

        // start session
        Session session = HibernateUtil.getSession();
        HibernateUtil.beginTransaction();

        // get the symbols
        @SuppressWarnings("unchecked") // should return a list of Symbols
        List<Symbol> result = session.createCriteria(Symbol.class)
                                        .add(Subqueries.propertyIn("symbolId", symbolIds))
                                        .addOrder(Order.asc("id"))
                                        .list();

        // load the lazy relations
        for (Symbol symb : result) {
            symb.loadLazyRelations();
        }

        // done
        HibernateUtil.commitTransaction();
        return result;
    }

    @Override
    public List<Symbol> getByIdsWithLatestRevision(Long projectId, Long... ids) {
        return getByIdsWithLatestRevision(projectId, SymbolVisibilityLevel.ALL, ids);
    }

    @Override
    public List<Symbol> getByIdsWithLatestRevision(Long projectId, SymbolVisibilityLevel visibilityLevel,
                                                      Long... ids) {
        // start session
        Session session = HibernateUtil.getSession();
        HibernateUtil.beginTransaction();

        // get latest revision
        @SuppressWarnings("unchecked") // should return a list of objects arrays which contain 2 Long values.
        List<Object[]> idRevList = session.createCriteria(Symbol.class)
                                            .add(Restrictions.eq("project.id", projectId))
                                            .add(Restrictions.in("id", ids))
                                            .add(visibilityLevel.getCriterion())
                                            .setProjection(Projections.projectionList()
                                                                       .add(Projections.groupProperty("id"))
                                                                       .add(Projections.max("revision"))
                                            ).list();

        HibernateUtil.commitTransaction();

        List<IdRevisionPair> idRevPairs = new LinkedList<>();
        for (Object[] obj : idRevList) {
            IdRevisionPair newPair = new IdRevisionPair();
            newPair.setId((Long) obj[0]);
            newPair.setRevision((Long) obj[1]);

            idRevPairs.add(newPair);
        }

        if (idRevPairs.isEmpty()) {
            return new LinkedList<>();
        }
        return getAll(projectId, idRevPairs);
    }

    @Override
    public List<Symbol> getAllWithLatestRevision(Long projectId, SymbolVisibilityLevel visibilityLevel) {
        // start session
        Session session = HibernateUtil.getSession();
        HibernateUtil.beginTransaction();

        @SuppressWarnings("unchecked") // should return a list of Symbols
        List<Long> ids = session.createCriteria(Symbol.class)
                                    .add(Restrictions.eq("project.id", projectId))
                                    .setProjection(Projections.property("id"))
                                    .list();

        HibernateUtil.commitTransaction();

        if (ids.isEmpty()) {
            return new LinkedList<>();
        }
        return getByIdsWithLatestRevision(projectId, visibilityLevel, ids.toArray(new Long[ids.size()]));
    }

    @Override
    public Symbol get(Long projectId, Long id, Long revision) {
        IdRevisionPair idRevisionPair = new IdRevisionPair(id, revision);
        List<IdRevisionPair> idRevisionList =  new LinkedList<>();
        idRevisionList.add(idRevisionPair);
        List<Symbol> resultList = getAll(projectId, idRevisionList);

        if (resultList.isEmpty()) {
            return null;
        }
        return resultList.get(0);
    }

    @Override
    public Symbol getWithLatestRevision(Long projectId, Long id) {
        List<Symbol> resultList = getByIdsWithLatestRevision(projectId, id);

        if (resultList.isEmpty()) {
            return null;
        }
        return resultList.get(0);
    }

    @Override
    public List<Symbol> getWithAllRevisions(Long projectId, Long id) {
        // start session
        Session session = HibernateUtil.getSession();
        HibernateUtil.beginTransaction();

        @SuppressWarnings("unchecked") // should return a list of revisions (type: Long)
        List<Long> revisions = session.createCriteria(Symbol.class)
                                        .add(Restrictions.eq("project.id", projectId))
                                        .add(Restrictions.eq("id", id))
                                        .setProjection(Projections.property("revision"))
                                        .addOrder(Order.asc("revision"))
                                        .list();

        HibernateUtil.commitTransaction();

        List<IdRevisionPair> idRevisionList =  new LinkedList<>();
        for (Long rev : revisions) {
            IdRevisionPair newPair = new IdRevisionPair(id, rev);
            idRevisionList.add(newPair);
        }
        List<Symbol> resultList = getAll(projectId, idRevisionList);

        return resultList;
    }

    @Override
    public void update(Symbol symbol) throws IllegalArgumentException, ValidationException {
        // checks for valid symbol
        if (symbol.getProjectId() == 0) {
            throw new IllegalArgumentException("Update failed: Project unknown.");
        }

        Symbol symbolInDB = getWithLatestRevision(symbol.getProjectId(), symbol.getId());
        if (symbolInDB == null) {
            throw new IllegalArgumentException("Update failed: Symbol unknown.");
        }

        SymbolGroup oldGroup = symbolGroupDAO.get(symbolInDB.getProjectId(), symbolInDB.getGroupId());
        SymbolGroup newGroup = symbolGroupDAO.get(symbol.getProjectId(), symbol.getGroupId());
        List<Symbol> symbols = getWithAllRevisions(symbol.getProjectId(), symbol.getId());
        System.out.println("%%%%% " + newGroup);

        // start session
        Session session = HibernateUtil.getSession();
        HibernateUtil.beginTransaction();

        // only allow update form the latest revision
        if (symbol.getRevision() != symbolInDB.getRevision()) {
            throw new IllegalArgumentException("Could not update the Symbol because you used an old revision.");
        }

        // update
        try {
            update(session, symbol, oldGroup);

            HibernateUtil.commitTransaction();

        // error handling
        } catch (javax.validation.ConstraintViolationException
                 | org.hibernate.exception.ConstraintViolationException e) {
            HibernateUtil.rollbackTransaction();
            throw new ValidationException("Could not update the Symbol because it is not valid.", e);
        }
    }

    private void update(Session session, Symbol symbol, SymbolGroup oldGroup) {
        // test for unique constrains
        checkUniqueConstrains(session, symbol); // will throw exception if the symbol is invalid

        System.out.println("==============================");
        SymbolGroup newGroup = (SymbolGroup) session.byNaturalId(SymbolGroup.class)
                                                    .using("project", symbol.getProject())
                                                    .using("id", symbol.getGroupId())
                                                    .load();
        System.out.println("$$$$$$ " + newGroup.getName());

        // count revision up
        symbol.setSymbolId(0L);
        symbol.setRevision(symbol.getRevision() + 1);
        symbol.setGroup(newGroup);

        symbol.beforeSave();
        session.save(symbol);

        System.out.println("==============================");

        if (!newGroup.equals(oldGroup)) {
//                oldGroup.getSymbols().removeAll(symbols);
//                session.update(oldGroup);
            System.out.println("$$$$$$ " + newGroup.getName());
            List<Symbol> symbols = session.createCriteria(Symbol.class)
                                            .add(Restrictions.eq("project", symbol.getProject()))
                                            .add(Restrictions.eq("id", symbol.getId()))
                                            .list();
            symbols.remove(symbol);

            for (Symbol s : symbols) {
//                    s.setGroup(newGroup);
                newGroup.addSymbol(s);
//                    session.update(s);
            }
//                session.update(newGroup);
        }
    }

    @Override
    public void hide(Long projectId, Long... ids) throws IllegalArgumentException {
        // start session
        Session session = HibernateUtil.getSession();
        HibernateUtil.beginTransaction();

        // update
        try {
            for (Long id : ids) {
                List<Symbol> symbols = getSymbols(session, projectId, id);

                hideSymbols(session, symbols);
            }
        } catch (IllegalArgumentException e) {
            HibernateUtil.rollbackTransaction();
            throw  e;
        }

        // done
        HibernateUtil.commitTransaction();
    }

    private void hideSymbols(Session session, List<Symbol> symbols) throws IllegalArgumentException {
        for (Symbol symbol : symbols) {
            symbol.loadLazyRelations();

            symbol.setHidden(true);
            session.update(symbol);
        }
    }

    @Override
    public void show(Long projectId, Long... ids) throws IllegalArgumentException {
        // start session
        Session session = HibernateUtil.getSession();
        HibernateUtil.beginTransaction();

        // update
        try {
            for (Long id : ids) {
                List<Symbol> symbols = getSymbols(session, projectId, id);
                showSymbols(session, symbols);
            }
        } catch (IllegalArgumentException e) {
            HibernateUtil.rollbackTransaction();
            throw  e;
        }

        // done
        HibernateUtil.commitTransaction();
    }

    private void showSymbols(Session session, List<Symbol> symbols) {
        for (Symbol symbol : symbols) {
            symbol.setHidden(false);
            session.update(symbol);
        }
    }

    private List<Symbol> getSymbols(Session session, Long projectId, Long symbolId) {
        @SuppressWarnings("should return a list of Symbols")
        List<Symbol> symbols = session.createCriteria(Symbol.class)
                                        .add(Restrictions.eq("project.id", projectId))
                                        .add(Restrictions.eq("id", symbolId))
                                        .list();

        if (symbols.size() == 0) {
            throw new IllegalArgumentException("Could not mark the symbol as hidden because it was not found.");
        }

        return symbols;
    }

    /**
     * Check the unique constrains of a symbol, e.g. a unique id, name or abbreviation per Project.
     * If the symbol is valid, nothing will happen, otherwise an exception will be thrown.
     * 
     * @param session
     *            The current session.
     * @param symbol
     *            The Symbol to check.
     * @throws IllegalArgumentException
     *             If the symbol faild the check.
     */
    private void checkUniqueConstrains(Session session, Symbol symbol) throws IllegalArgumentException {
        // put constrains into a query
        Junction restrictions = Restrictions.conjunction()
                                    .add(Restrictions.eq("project", symbol.getProject()))
                                    .add(Restrictions.disjunction()
                                                 .add(Restrictions.eq("name", symbol.getName()))
                                                 .add(Restrictions.eq("abbreviation", symbol.getAbbreviation())));
        if (symbol.getId() != null) {
            restrictions = restrictions.add(Restrictions.ne("id", symbol.getId()));
        }

        @SuppressWarnings("unchecked") // should return list of symbols
        List<Symbol> testList = session.createCriteria(symbol.getClass())
                                            .add(restrictions)
                                            .list();

        // if the query result is not empty, the constrains are violated.
        if (testList.size() > 0) {
            System.out.println("checkUniqueConstrains();");
            HibernateUtil.rollbackTransaction();
            throw new ValidationException("The name or the abbreviation of the symbol is already used in the project.");
        }
    }

}
