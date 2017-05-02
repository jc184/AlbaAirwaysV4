/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.RollbackFailureException;
import entities.Booking;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Customer;
import entities.Flight;
import entities.Seat;
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
public class BookingJpaController implements Serializable {

    public BookingJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Booking booking) throws RollbackFailureException, Exception {
        if (booking.getSeatCollection() == null) {
            booking.setSeatCollection(new ArrayList<Seat>());
        }
        if (booking.getPassengerCollection() == null) {
            booking.setPassengerCollection(new ArrayList<Passenger>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Customer customerId = booking.getCustomerId();
            if (customerId != null) {
                customerId = em.getReference(customerId.getClass(), customerId.getCustomerId());
                booking.setCustomerId(customerId);
            }
            Flight flightId = booking.getFlightId();
            if (flightId != null) {
                flightId = em.getReference(flightId.getClass(), flightId.getFlightId());
                booking.setFlightId(flightId);
            }
            Collection<Seat> attachedSeatCollection = new ArrayList<Seat>();
            for (Seat seatCollectionSeatToAttach : booking.getSeatCollection()) {
                seatCollectionSeatToAttach = em.getReference(seatCollectionSeatToAttach.getClass(), seatCollectionSeatToAttach.getSeatNo());
                attachedSeatCollection.add(seatCollectionSeatToAttach);
            }
            booking.setSeatCollection(attachedSeatCollection);
            Collection<Passenger> attachedPassengerCollection = new ArrayList<Passenger>();
            for (Passenger passengerCollectionPassengerToAttach : booking.getPassengerCollection()) {
                passengerCollectionPassengerToAttach = em.getReference(passengerCollectionPassengerToAttach.getClass(), passengerCollectionPassengerToAttach.getPassengerId());
                attachedPassengerCollection.add(passengerCollectionPassengerToAttach);
            }
            booking.setPassengerCollection(attachedPassengerCollection);
            em.persist(booking);
            if (customerId != null) {
                customerId.getBookingCollection().add(booking);
                customerId = em.merge(customerId);
            }
            if (flightId != null) {
                flightId.getBookingCollection().add(booking);
                flightId = em.merge(flightId);
            }
            for (Seat seatCollectionSeat : booking.getSeatCollection()) {
                Booking oldBookingIdOfSeatCollectionSeat = seatCollectionSeat.getBookingId();
                seatCollectionSeat.setBookingId(booking);
                seatCollectionSeat = em.merge(seatCollectionSeat);
                if (oldBookingIdOfSeatCollectionSeat != null) {
                    oldBookingIdOfSeatCollectionSeat.getSeatCollection().remove(seatCollectionSeat);
                    oldBookingIdOfSeatCollectionSeat = em.merge(oldBookingIdOfSeatCollectionSeat);
                }
            }
            for (Passenger passengerCollectionPassenger : booking.getPassengerCollection()) {
                Booking oldBookingIdOfPassengerCollectionPassenger = passengerCollectionPassenger.getBookingId();
                passengerCollectionPassenger.setBookingId(booking);
                passengerCollectionPassenger = em.merge(passengerCollectionPassenger);
                if (oldBookingIdOfPassengerCollectionPassenger != null) {
                    oldBookingIdOfPassengerCollectionPassenger.getPassengerCollection().remove(passengerCollectionPassenger);
                    oldBookingIdOfPassengerCollectionPassenger = em.merge(oldBookingIdOfPassengerCollectionPassenger);
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

    public void edit(Booking booking) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Booking persistentBooking = em.find(Booking.class, booking.getBookingId());
            Customer customerIdOld = persistentBooking.getCustomerId();
            Customer customerIdNew = booking.getCustomerId();
            Flight flightIdOld = persistentBooking.getFlightId();
            Flight flightIdNew = booking.getFlightId();
            Collection<Seat> seatCollectionOld = persistentBooking.getSeatCollection();
            Collection<Seat> seatCollectionNew = booking.getSeatCollection();
            Collection<Passenger> passengerCollectionOld = persistentBooking.getPassengerCollection();
            Collection<Passenger> passengerCollectionNew = booking.getPassengerCollection();
            List<String> illegalOrphanMessages = null;
            for (Passenger passengerCollectionOldPassenger : passengerCollectionOld) {
                if (!passengerCollectionNew.contains(passengerCollectionOldPassenger)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Passenger " + passengerCollectionOldPassenger + " since its bookingId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (customerIdNew != null) {
                customerIdNew = em.getReference(customerIdNew.getClass(), customerIdNew.getCustomerId());
                booking.setCustomerId(customerIdNew);
            }
            if (flightIdNew != null) {
                flightIdNew = em.getReference(flightIdNew.getClass(), flightIdNew.getFlightId());
                booking.setFlightId(flightIdNew);
            }
            Collection<Seat> attachedSeatCollectionNew = new ArrayList<Seat>();
            for (Seat seatCollectionNewSeatToAttach : seatCollectionNew) {
                seatCollectionNewSeatToAttach = em.getReference(seatCollectionNewSeatToAttach.getClass(), seatCollectionNewSeatToAttach.getSeatNo());
                attachedSeatCollectionNew.add(seatCollectionNewSeatToAttach);
            }
            seatCollectionNew = attachedSeatCollectionNew;
            booking.setSeatCollection(seatCollectionNew);
            Collection<Passenger> attachedPassengerCollectionNew = new ArrayList<Passenger>();
            for (Passenger passengerCollectionNewPassengerToAttach : passengerCollectionNew) {
                passengerCollectionNewPassengerToAttach = em.getReference(passengerCollectionNewPassengerToAttach.getClass(), passengerCollectionNewPassengerToAttach.getPassengerId());
                attachedPassengerCollectionNew.add(passengerCollectionNewPassengerToAttach);
            }
            passengerCollectionNew = attachedPassengerCollectionNew;
            booking.setPassengerCollection(passengerCollectionNew);
            booking = em.merge(booking);
            if (customerIdOld != null && !customerIdOld.equals(customerIdNew)) {
                customerIdOld.getBookingCollection().remove(booking);
                customerIdOld = em.merge(customerIdOld);
            }
            if (customerIdNew != null && !customerIdNew.equals(customerIdOld)) {
                customerIdNew.getBookingCollection().add(booking);
                customerIdNew = em.merge(customerIdNew);
            }
            if (flightIdOld != null && !flightIdOld.equals(flightIdNew)) {
                flightIdOld.getBookingCollection().remove(booking);
                flightIdOld = em.merge(flightIdOld);
            }
            if (flightIdNew != null && !flightIdNew.equals(flightIdOld)) {
                flightIdNew.getBookingCollection().add(booking);
                flightIdNew = em.merge(flightIdNew);
            }
            for (Seat seatCollectionOldSeat : seatCollectionOld) {
                if (!seatCollectionNew.contains(seatCollectionOldSeat)) {
                    seatCollectionOldSeat.setBookingId(null);
                    seatCollectionOldSeat = em.merge(seatCollectionOldSeat);
                }
            }
            for (Seat seatCollectionNewSeat : seatCollectionNew) {
                if (!seatCollectionOld.contains(seatCollectionNewSeat)) {
                    Booking oldBookingIdOfSeatCollectionNewSeat = seatCollectionNewSeat.getBookingId();
                    seatCollectionNewSeat.setBookingId(booking);
                    seatCollectionNewSeat = em.merge(seatCollectionNewSeat);
                    if (oldBookingIdOfSeatCollectionNewSeat != null && !oldBookingIdOfSeatCollectionNewSeat.equals(booking)) {
                        oldBookingIdOfSeatCollectionNewSeat.getSeatCollection().remove(seatCollectionNewSeat);
                        oldBookingIdOfSeatCollectionNewSeat = em.merge(oldBookingIdOfSeatCollectionNewSeat);
                    }
                }
            }
            for (Passenger passengerCollectionNewPassenger : passengerCollectionNew) {
                if (!passengerCollectionOld.contains(passengerCollectionNewPassenger)) {
                    Booking oldBookingIdOfPassengerCollectionNewPassenger = passengerCollectionNewPassenger.getBookingId();
                    passengerCollectionNewPassenger.setBookingId(booking);
                    passengerCollectionNewPassenger = em.merge(passengerCollectionNewPassenger);
                    if (oldBookingIdOfPassengerCollectionNewPassenger != null && !oldBookingIdOfPassengerCollectionNewPassenger.equals(booking)) {
                        oldBookingIdOfPassengerCollectionNewPassenger.getPassengerCollection().remove(passengerCollectionNewPassenger);
                        oldBookingIdOfPassengerCollectionNewPassenger = em.merge(oldBookingIdOfPassengerCollectionNewPassenger);
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
                Integer id = booking.getBookingId();
                if (findBooking(id) == null) {
                    throw new NonexistentEntityException("The booking with id " + id + " no longer exists.");
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
            Booking booking;
            try {
                booking = em.getReference(Booking.class, id);
                booking.getBookingId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The booking with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Passenger> passengerCollectionOrphanCheck = booking.getPassengerCollection();
            for (Passenger passengerCollectionOrphanCheckPassenger : passengerCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Booking (" + booking + ") cannot be destroyed since the Passenger " + passengerCollectionOrphanCheckPassenger + " in its passengerCollection field has a non-nullable bookingId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Customer customerId = booking.getCustomerId();
            if (customerId != null) {
                customerId.getBookingCollection().remove(booking);
                customerId = em.merge(customerId);
            }
            Flight flightId = booking.getFlightId();
            if (flightId != null) {
                flightId.getBookingCollection().remove(booking);
                flightId = em.merge(flightId);
            }
            Collection<Seat> seatCollection = booking.getSeatCollection();
            for (Seat seatCollectionSeat : seatCollection) {
                seatCollectionSeat.setBookingId(null);
                seatCollectionSeat = em.merge(seatCollectionSeat);
            }
            em.remove(booking);
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

    public List<Booking> findBookingEntities() {
        return findBookingEntities(true, -1, -1);
    }

    public List<Booking> findBookingEntities(int maxResults, int firstResult) {
        return findBookingEntities(false, maxResults, firstResult);
    }

    private List<Booking> findBookingEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Booking.class));
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

    public Booking findBooking(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Booking.class, id);
        } finally {
            em.close();
        }
    }

    public int getBookingCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Booking> rt = cq.from(Booking.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
