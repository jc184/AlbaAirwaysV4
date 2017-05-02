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
import entities.Route;
import entities.Booking;
import entities.Flight;
import java.util.ArrayList;
import java.util.Collection;
import entities.Passenger;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author james
 */
public class FlightJpaController implements Serializable {

    public FlightJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Flight flight) throws RollbackFailureException, Exception {
        if (flight.getBookingCollection() == null) {
            flight.setBookingCollection(new ArrayList<Booking>());
        }
        if (flight.getPassengerCollection() == null) {
            flight.setPassengerCollection(new ArrayList<Passenger>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Route routeId = flight.getRouteId();
            if (routeId != null) {
                routeId = em.getReference(routeId.getClass(), routeId.getRouteId());
                flight.setRouteId(routeId);
            }
            Collection<Booking> attachedBookingCollection = new ArrayList<Booking>();
            for (Booking bookingCollectionBookingToAttach : flight.getBookingCollection()) {
                bookingCollectionBookingToAttach = em.getReference(bookingCollectionBookingToAttach.getClass(), bookingCollectionBookingToAttach.getBookingId());
                attachedBookingCollection.add(bookingCollectionBookingToAttach);
            }
            flight.setBookingCollection(attachedBookingCollection);
            Collection<Passenger> attachedPassengerCollection = new ArrayList<Passenger>();
            for (Passenger passengerCollectionPassengerToAttach : flight.getPassengerCollection()) {
                passengerCollectionPassengerToAttach = em.getReference(passengerCollectionPassengerToAttach.getClass(), passengerCollectionPassengerToAttach.getPassengerId());
                attachedPassengerCollection.add(passengerCollectionPassengerToAttach);
            }
            flight.setPassengerCollection(attachedPassengerCollection);
            em.persist(flight);
            if (routeId != null) {
                routeId.getFlightCollection().add(flight);
                routeId = em.merge(routeId);
            }
            for (Booking bookingCollectionBooking : flight.getBookingCollection()) {
                Flight oldFlightIdOfBookingCollectionBooking = bookingCollectionBooking.getFlightId();
                bookingCollectionBooking.setFlightId(flight);
                bookingCollectionBooking = em.merge(bookingCollectionBooking);
                if (oldFlightIdOfBookingCollectionBooking != null) {
                    oldFlightIdOfBookingCollectionBooking.getBookingCollection().remove(bookingCollectionBooking);
                    oldFlightIdOfBookingCollectionBooking = em.merge(oldFlightIdOfBookingCollectionBooking);
                }
            }
            for (Passenger passengerCollectionPassenger : flight.getPassengerCollection()) {
                Flight oldFlightIdOfPassengerCollectionPassenger = passengerCollectionPassenger.getFlightId();
                passengerCollectionPassenger.setFlightId(flight);
                passengerCollectionPassenger = em.merge(passengerCollectionPassenger);
                if (oldFlightIdOfPassengerCollectionPassenger != null) {
                    oldFlightIdOfPassengerCollectionPassenger.getPassengerCollection().remove(passengerCollectionPassenger);
                    oldFlightIdOfPassengerCollectionPassenger = em.merge(oldFlightIdOfPassengerCollectionPassenger);
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

    public void edit(Flight flight) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Flight persistentFlight = em.find(Flight.class, flight.getFlightId());
            Route routeIdOld = persistentFlight.getRouteId();
            Route routeIdNew = flight.getRouteId();
            Collection<Booking> bookingCollectionOld = persistentFlight.getBookingCollection();
            Collection<Booking> bookingCollectionNew = flight.getBookingCollection();
            Collection<Passenger> passengerCollectionOld = persistentFlight.getPassengerCollection();
            Collection<Passenger> passengerCollectionNew = flight.getPassengerCollection();
            List<String> illegalOrphanMessages = null;
            for (Booking bookingCollectionOldBooking : bookingCollectionOld) {
                if (!bookingCollectionNew.contains(bookingCollectionOldBooking)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Booking " + bookingCollectionOldBooking + " since its flightId field is not nullable.");
                }
            }
            for (Passenger passengerCollectionOldPassenger : passengerCollectionOld) {
                if (!passengerCollectionNew.contains(passengerCollectionOldPassenger)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Passenger " + passengerCollectionOldPassenger + " since its flightId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (routeIdNew != null) {
                routeIdNew = em.getReference(routeIdNew.getClass(), routeIdNew.getRouteId());
                flight.setRouteId(routeIdNew);
            }
            Collection<Booking> attachedBookingCollectionNew = new ArrayList<Booking>();
            for (Booking bookingCollectionNewBookingToAttach : bookingCollectionNew) {
                bookingCollectionNewBookingToAttach = em.getReference(bookingCollectionNewBookingToAttach.getClass(), bookingCollectionNewBookingToAttach.getBookingId());
                attachedBookingCollectionNew.add(bookingCollectionNewBookingToAttach);
            }
            bookingCollectionNew = attachedBookingCollectionNew;
            flight.setBookingCollection(bookingCollectionNew);
            Collection<Passenger> attachedPassengerCollectionNew = new ArrayList<Passenger>();
            for (Passenger passengerCollectionNewPassengerToAttach : passengerCollectionNew) {
                passengerCollectionNewPassengerToAttach = em.getReference(passengerCollectionNewPassengerToAttach.getClass(), passengerCollectionNewPassengerToAttach.getPassengerId());
                attachedPassengerCollectionNew.add(passengerCollectionNewPassengerToAttach);
            }
            passengerCollectionNew = attachedPassengerCollectionNew;
            flight.setPassengerCollection(passengerCollectionNew);
            flight = em.merge(flight);
            if (routeIdOld != null && !routeIdOld.equals(routeIdNew)) {
                routeIdOld.getFlightCollection().remove(flight);
                routeIdOld = em.merge(routeIdOld);
            }
            if (routeIdNew != null && !routeIdNew.equals(routeIdOld)) {
                routeIdNew.getFlightCollection().add(flight);
                routeIdNew = em.merge(routeIdNew);
            }
            for (Booking bookingCollectionNewBooking : bookingCollectionNew) {
                if (!bookingCollectionOld.contains(bookingCollectionNewBooking)) {
                    Flight oldFlightIdOfBookingCollectionNewBooking = bookingCollectionNewBooking.getFlightId();
                    bookingCollectionNewBooking.setFlightId(flight);
                    bookingCollectionNewBooking = em.merge(bookingCollectionNewBooking);
                    if (oldFlightIdOfBookingCollectionNewBooking != null && !oldFlightIdOfBookingCollectionNewBooking.equals(flight)) {
                        oldFlightIdOfBookingCollectionNewBooking.getBookingCollection().remove(bookingCollectionNewBooking);
                        oldFlightIdOfBookingCollectionNewBooking = em.merge(oldFlightIdOfBookingCollectionNewBooking);
                    }
                }
            }
            for (Passenger passengerCollectionNewPassenger : passengerCollectionNew) {
                if (!passengerCollectionOld.contains(passengerCollectionNewPassenger)) {
                    Flight oldFlightIdOfPassengerCollectionNewPassenger = passengerCollectionNewPassenger.getFlightId();
                    passengerCollectionNewPassenger.setFlightId(flight);
                    passengerCollectionNewPassenger = em.merge(passengerCollectionNewPassenger);
                    if (oldFlightIdOfPassengerCollectionNewPassenger != null && !oldFlightIdOfPassengerCollectionNewPassenger.equals(flight)) {
                        oldFlightIdOfPassengerCollectionNewPassenger.getPassengerCollection().remove(passengerCollectionNewPassenger);
                        oldFlightIdOfPassengerCollectionNewPassenger = em.merge(oldFlightIdOfPassengerCollectionNewPassenger);
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
                Integer id = flight.getFlightId();
                if (findFlight(id) == null) {
                    throw new NonexistentEntityException("The flight with id " + id + " no longer exists.");
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
            Flight flight;
            try {
                flight = em.getReference(Flight.class, id);
                flight.getFlightId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The flight with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Booking> bookingCollectionOrphanCheck = flight.getBookingCollection();
            for (Booking bookingCollectionOrphanCheckBooking : bookingCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Flight (" + flight + ") cannot be destroyed since the Booking " + bookingCollectionOrphanCheckBooking + " in its bookingCollection field has a non-nullable flightId field.");
            }
            Collection<Passenger> passengerCollectionOrphanCheck = flight.getPassengerCollection();
            for (Passenger passengerCollectionOrphanCheckPassenger : passengerCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Flight (" + flight + ") cannot be destroyed since the Passenger " + passengerCollectionOrphanCheckPassenger + " in its passengerCollection field has a non-nullable flightId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Route routeId = flight.getRouteId();
            if (routeId != null) {
                routeId.getFlightCollection().remove(flight);
                routeId = em.merge(routeId);
            }
            em.remove(flight);
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

    public List<Flight> findFlightEntities() {
        return findFlightEntities(true, -1, -1);
    }

    public List<Flight> findFlightEntities(int maxResults, int firstResult) {
        return findFlightEntities(false, maxResults, firstResult);
    }

    private List<Flight> findFlightEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Flight.class));
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

    public Flight findFlight(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Flight.class, id);
        } finally {
            em.close();
        }
    }

    public int getFlightCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Flight> rt = cq.from(Flight.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
