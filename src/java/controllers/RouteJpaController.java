/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Flight;
import entities.Route;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author james
 */
public class RouteJpaController implements Serializable {

    public RouteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Route route) throws RollbackFailureException, Exception {
        if (route.getFlightCollection() == null) {
            route.setFlightCollection(new ArrayList<Flight>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Flight> attachedFlightCollection = new ArrayList<Flight>();
            for (Flight flightCollectionFlightToAttach : route.getFlightCollection()) {
                flightCollectionFlightToAttach = em.getReference(flightCollectionFlightToAttach.getClass(), flightCollectionFlightToAttach.getFlightId());
                attachedFlightCollection.add(flightCollectionFlightToAttach);
            }
            route.setFlightCollection(attachedFlightCollection);
            em.persist(route);
            for (Flight flightCollectionFlight : route.getFlightCollection()) {
                Route oldRouteIdOfFlightCollectionFlight = flightCollectionFlight.getRouteId();
                flightCollectionFlight.setRouteId(route);
                flightCollectionFlight = em.merge(flightCollectionFlight);
                if (oldRouteIdOfFlightCollectionFlight != null) {
                    oldRouteIdOfFlightCollectionFlight.getFlightCollection().remove(flightCollectionFlight);
                    oldRouteIdOfFlightCollectionFlight = em.merge(oldRouteIdOfFlightCollectionFlight);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Route route) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Route persistentRoute = em.find(Route.class, route.getRouteId());
            Collection<Flight> flightCollectionOld = persistentRoute.getFlightCollection();
            Collection<Flight> flightCollectionNew = route.getFlightCollection();
            List<String> illegalOrphanMessages = null;
            for (Flight flightCollectionOldFlight : flightCollectionOld) {
                if (!flightCollectionNew.contains(flightCollectionOldFlight)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Flight " + flightCollectionOldFlight + " since its routeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Flight> attachedFlightCollectionNew = new ArrayList<Flight>();
            for (Flight flightCollectionNewFlightToAttach : flightCollectionNew) {
                flightCollectionNewFlightToAttach = em.getReference(flightCollectionNewFlightToAttach.getClass(), flightCollectionNewFlightToAttach.getFlightId());
                attachedFlightCollectionNew.add(flightCollectionNewFlightToAttach);
            }
            flightCollectionNew = attachedFlightCollectionNew;
            route.setFlightCollection(flightCollectionNew);
            route = em.merge(route);
            for (Flight flightCollectionNewFlight : flightCollectionNew) {
                if (!flightCollectionOld.contains(flightCollectionNewFlight)) {
                    Route oldRouteIdOfFlightCollectionNewFlight = flightCollectionNewFlight.getRouteId();
                    flightCollectionNewFlight.setRouteId(route);
                    flightCollectionNewFlight = em.merge(flightCollectionNewFlight);
                    if (oldRouteIdOfFlightCollectionNewFlight != null && !oldRouteIdOfFlightCollectionNewFlight.equals(route)) {
                        oldRouteIdOfFlightCollectionNewFlight.getFlightCollection().remove(flightCollectionNewFlight);
                        oldRouteIdOfFlightCollectionNewFlight = em.merge(oldRouteIdOfFlightCollectionNewFlight);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = route.getRouteId();
                if (findRoute(id) == null) {
                    throw new NonexistentEntityException("The route with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Route route;
            try {
                route = em.getReference(Route.class, id);
                route.getRouteId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The route with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Flight> flightCollectionOrphanCheck = route.getFlightCollection();
            for (Flight flightCollectionOrphanCheckFlight : flightCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Route (" + route + ") cannot be destroyed since the Flight " + flightCollectionOrphanCheckFlight + " in its flightCollection field has a non-nullable routeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(route);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Route> findRouteEntities() {
        return findRouteEntities(true, -1, -1);
    }

    public List<Route> findRouteEntities(int maxResults, int firstResult) {
        return findRouteEntities(false, maxResults, firstResult);
    }

    private List<Route> findRouteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Route.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Route findRoute(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Route.class, id);
        } finally {
            em.close();
        }
    }

    public int getRouteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Route> rt = cq.from(Route.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
