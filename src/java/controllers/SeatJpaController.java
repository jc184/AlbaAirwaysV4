/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.PreexistingEntityException;
import controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Booking;
import entities.Passenger;
import entities.Seat;
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
public class SeatJpaController implements Serializable {

    public SeatJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Seat seat) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (seat.getPassengerCollection() == null) {
            seat.setPassengerCollection(new ArrayList<Passenger>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Booking bookingId = seat.getBookingId();
            if (bookingId != null) {
                bookingId = em.getReference(bookingId.getClass(), bookingId.getBookingId());
                seat.setBookingId(bookingId);
            }
            Collection<Passenger> attachedPassengerCollection = new ArrayList<Passenger>();
            for (Passenger passengerCollectionPassengerToAttach : seat.getPassengerCollection()) {
                passengerCollectionPassengerToAttach = em.getReference(passengerCollectionPassengerToAttach.getClass(), passengerCollectionPassengerToAttach.getPassengerId());
                attachedPassengerCollection.add(passengerCollectionPassengerToAttach);
            }
            seat.setPassengerCollection(attachedPassengerCollection);
            em.persist(seat);
            if (bookingId != null) {
                bookingId.getSeatCollection().add(seat);
                bookingId = em.merge(bookingId);
            }
            for (Passenger passengerCollectionPassenger : seat.getPassengerCollection()) {
                Seat oldSeatNoOfPassengerCollectionPassenger = passengerCollectionPassenger.getSeatNo();
                passengerCollectionPassenger.setSeatNo(seat);
                passengerCollectionPassenger = em.merge(passengerCollectionPassenger);
                if (oldSeatNoOfPassengerCollectionPassenger != null) {
                    oldSeatNoOfPassengerCollectionPassenger.getPassengerCollection().remove(passengerCollectionPassenger);
                    oldSeatNoOfPassengerCollectionPassenger = em.merge(oldSeatNoOfPassengerCollectionPassenger);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findSeat(seat.getSeatNo()) != null) {
                throw new PreexistingEntityException("Seat " + seat + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Seat seat) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Seat persistentSeat = em.find(Seat.class, seat.getSeatNo());
            Booking bookingIdOld = persistentSeat.getBookingId();
            Booking bookingIdNew = seat.getBookingId();
            Collection<Passenger> passengerCollectionOld = persistentSeat.getPassengerCollection();
            Collection<Passenger> passengerCollectionNew = seat.getPassengerCollection();
            List<String> illegalOrphanMessages = null;
            for (Passenger passengerCollectionOldPassenger : passengerCollectionOld) {
                if (!passengerCollectionNew.contains(passengerCollectionOldPassenger)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Passenger " + passengerCollectionOldPassenger + " since its seatNo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (bookingIdNew != null) {
                bookingIdNew = em.getReference(bookingIdNew.getClass(), bookingIdNew.getBookingId());
                seat.setBookingId(bookingIdNew);
            }
            Collection<Passenger> attachedPassengerCollectionNew = new ArrayList<Passenger>();
            for (Passenger passengerCollectionNewPassengerToAttach : passengerCollectionNew) {
                passengerCollectionNewPassengerToAttach = em.getReference(passengerCollectionNewPassengerToAttach.getClass(), passengerCollectionNewPassengerToAttach.getPassengerId());
                attachedPassengerCollectionNew.add(passengerCollectionNewPassengerToAttach);
            }
            passengerCollectionNew = attachedPassengerCollectionNew;
            seat.setPassengerCollection(passengerCollectionNew);
            seat = em.merge(seat);
            if (bookingIdOld != null && !bookingIdOld.equals(bookingIdNew)) {
                bookingIdOld.getSeatCollection().remove(seat);
                bookingIdOld = em.merge(bookingIdOld);
            }
            if (bookingIdNew != null && !bookingIdNew.equals(bookingIdOld)) {
                bookingIdNew.getSeatCollection().add(seat);
                bookingIdNew = em.merge(bookingIdNew);
            }
            for (Passenger passengerCollectionNewPassenger : passengerCollectionNew) {
                if (!passengerCollectionOld.contains(passengerCollectionNewPassenger)) {
                    Seat oldSeatNoOfPassengerCollectionNewPassenger = passengerCollectionNewPassenger.getSeatNo();
                    passengerCollectionNewPassenger.setSeatNo(seat);
                    passengerCollectionNewPassenger = em.merge(passengerCollectionNewPassenger);
                    if (oldSeatNoOfPassengerCollectionNewPassenger != null && !oldSeatNoOfPassengerCollectionNewPassenger.equals(seat)) {
                        oldSeatNoOfPassengerCollectionNewPassenger.getPassengerCollection().remove(passengerCollectionNewPassenger);
                        oldSeatNoOfPassengerCollectionNewPassenger = em.merge(oldSeatNoOfPassengerCollectionNewPassenger);
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
                Integer id = seat.getSeatNo();
                if (findSeat(id) == null) {
                    throw new NonexistentEntityException("The seat with id " + id + " no longer exists.");
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
            Seat seat;
            try {
                seat = em.getReference(Seat.class, id);
                seat.getSeatNo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The seat with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Passenger> passengerCollectionOrphanCheck = seat.getPassengerCollection();
            for (Passenger passengerCollectionOrphanCheckPassenger : passengerCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Seat (" + seat + ") cannot be destroyed since the Passenger " + passengerCollectionOrphanCheckPassenger + " in its passengerCollection field has a non-nullable seatNo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Booking bookingId = seat.getBookingId();
            if (bookingId != null) {
                bookingId.getSeatCollection().remove(seat);
                bookingId = em.merge(bookingId);
            }
            em.remove(seat);
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

    public List<Seat> findSeatEntities() {
        return findSeatEntities(true, -1, -1);
    }

    public List<Seat> findSeatEntities(int maxResults, int firstResult) {
        return findSeatEntities(false, maxResults, firstResult);
    }

    private List<Seat> findSeatEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Seat.class));
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

    public Seat findSeat(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Seat.class, id);
        } finally {
            em.close();
        }
    }

    public int getSeatCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Seat> rt = cq.from(Seat.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
