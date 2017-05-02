/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Booking;
import entities.Flight;
import entities.Passenger;
import entities.Seat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author james
 */
public class PassengerJpaController implements Serializable {

    public PassengerJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Passenger passenger) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Booking bookingId = passenger.getBookingId();
            if (bookingId != null) {
                bookingId = em.getReference(bookingId.getClass(), bookingId.getBookingId());
                passenger.setBookingId(bookingId);
            }
            Flight flightId = passenger.getFlightId();
            if (flightId != null) {
                flightId = em.getReference(flightId.getClass(), flightId.getFlightId());
                passenger.setFlightId(flightId);
            }
            Seat seatNo = passenger.getSeatNo();
            if (seatNo != null) {
                seatNo = em.getReference(seatNo.getClass(), seatNo.getSeatNo());
                passenger.setSeatNo(seatNo);
            }
            em.persist(passenger);
            if (bookingId != null) {
                bookingId.getPassengerCollection().add(passenger);
                bookingId = em.merge(bookingId);
            }
            if (flightId != null) {
                flightId.getPassengerCollection().add(passenger);
                flightId = em.merge(flightId);
            }
            if (seatNo != null) {
                seatNo.getPassengerCollection().add(passenger);
                seatNo = em.merge(seatNo);
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

    public void edit(Passenger passenger) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Passenger persistentPassenger = em.find(Passenger.class, passenger.getPassengerId());
            Booking bookingIdOld = persistentPassenger.getBookingId();
            Booking bookingIdNew = passenger.getBookingId();
            Flight flightIdOld = persistentPassenger.getFlightId();
            Flight flightIdNew = passenger.getFlightId();
            Seat seatNoOld = persistentPassenger.getSeatNo();
            Seat seatNoNew = passenger.getSeatNo();
            if (bookingIdNew != null) {
                bookingIdNew = em.getReference(bookingIdNew.getClass(), bookingIdNew.getBookingId());
                passenger.setBookingId(bookingIdNew);
            }
            if (flightIdNew != null) {
                flightIdNew = em.getReference(flightIdNew.getClass(), flightIdNew.getFlightId());
                passenger.setFlightId(flightIdNew);
            }
            if (seatNoNew != null) {
                seatNoNew = em.getReference(seatNoNew.getClass(), seatNoNew.getSeatNo());
                passenger.setSeatNo(seatNoNew);
            }
            passenger = em.merge(passenger);
            if (bookingIdOld != null && !bookingIdOld.equals(bookingIdNew)) {
                bookingIdOld.getPassengerCollection().remove(passenger);
                bookingIdOld = em.merge(bookingIdOld);
            }
            if (bookingIdNew != null && !bookingIdNew.equals(bookingIdOld)) {
                bookingIdNew.getPassengerCollection().add(passenger);
                bookingIdNew = em.merge(bookingIdNew);
            }
            if (flightIdOld != null && !flightIdOld.equals(flightIdNew)) {
                flightIdOld.getPassengerCollection().remove(passenger);
                flightIdOld = em.merge(flightIdOld);
            }
            if (flightIdNew != null && !flightIdNew.equals(flightIdOld)) {
                flightIdNew.getPassengerCollection().add(passenger);
                flightIdNew = em.merge(flightIdNew);
            }
            if (seatNoOld != null && !seatNoOld.equals(seatNoNew)) {
                seatNoOld.getPassengerCollection().remove(passenger);
                seatNoOld = em.merge(seatNoOld);
            }
            if (seatNoNew != null && !seatNoNew.equals(seatNoOld)) {
                seatNoNew.getPassengerCollection().add(passenger);
                seatNoNew = em.merge(seatNoNew);
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
                Integer id = passenger.getPassengerId();
                if (findPassenger(id) == null) {
                    throw new NonexistentEntityException("The passenger with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Passenger passenger;
            try {
                passenger = em.getReference(Passenger.class, id);
                passenger.getPassengerId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The passenger with id " + id + " no longer exists.", enfe);
            }
            Booking bookingId = passenger.getBookingId();
            if (bookingId != null) {
                bookingId.getPassengerCollection().remove(passenger);
                bookingId = em.merge(bookingId);
            }
            Flight flightId = passenger.getFlightId();
            if (flightId != null) {
                flightId.getPassengerCollection().remove(passenger);
                flightId = em.merge(flightId);
            }
            Seat seatNo = passenger.getSeatNo();
            if (seatNo != null) {
                seatNo.getPassengerCollection().remove(passenger);
                seatNo = em.merge(seatNo);
            }
            em.remove(passenger);
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

    public List<Passenger> findPassengerEntities() {
        return findPassengerEntities(true, -1, -1);
    }

    public List<Passenger> findPassengerEntities(int maxResults, int firstResult) {
        return findPassengerEntities(false, maxResults, firstResult);
    }

    private List<Passenger> findPassengerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Passenger.class));
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

    public Passenger findPassenger(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Passenger.class, id);
        } finally {
            em.close();
        }
    }

    public int getPassengerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Passenger> rt = cq.from(Passenger.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
