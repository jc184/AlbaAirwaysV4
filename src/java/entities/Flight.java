/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author james
 */
@Entity
@Table(name = "flight")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Flight.findAll", query = "SELECT f FROM Flight f")
    , @NamedQuery(name = "Flight.findByFlightId", query = "SELECT f FROM Flight f WHERE f.flightId = :flightId")
    , @NamedQuery(name = "Flight.findByFlightDate", query = "SELECT f FROM Flight f WHERE f.flightDate = :flightDate")
    , @NamedQuery(name = "Flight.findByPassengerCount", query = "SELECT f FROM Flight f WHERE f.passengerCount = :passengerCount")
    , @NamedQuery(name = "Flight.findByLeaveDateTime", query = "SELECT f FROM Flight f WHERE f.leaveDateTime = :leaveDateTime")
    , @NamedQuery(name = "Flight.findByArrivalDateTime", query = "SELECT f FROM Flight f WHERE f.arrivalDateTime = :arrivalDateTime")})
public class Flight implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "FlightId")
    private Integer flightId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FlightDate")
    @Temporal(TemporalType.DATE)
    private Date flightDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PassengerCount")
    private int passengerCount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "LeaveDateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date leaveDateTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ArrivalDateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date arrivalDateTime;
    @JoinColumn(name = "RouteId", referencedColumnName = "RouteId")
    @ManyToOne(optional = false)
    private Route routeId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "flightId")
    private Collection<Booking> bookingCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "flightId")
    private Collection<Passenger> passengerCollection;

    public Flight() {
    }

    public Flight(Integer flightId) {
        this.flightId = flightId;
    }

    public Flight(Integer flightId, Date flightDate, int passengerCount, Date leaveDateTime, Date arrivalDateTime) {
        this.flightId = flightId;
        this.flightDate = flightDate;
        this.passengerCount = passengerCount;
        this.leaveDateTime = leaveDateTime;
        this.arrivalDateTime = arrivalDateTime;
    }

    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public Date getLeaveDateTime() {
        return leaveDateTime;
    }

    public void setLeaveDateTime(Date leaveDateTime) {
        this.leaveDateTime = leaveDateTime;
    }

    public Date getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(Date arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public Route getRouteId() {
        return routeId;
    }

    public void setRouteId(Route routeId) {
        this.routeId = routeId;
    }

    @XmlTransient
    public Collection<Booking> getBookingCollection() {
        return bookingCollection;
    }

    public void setBookingCollection(Collection<Booking> bookingCollection) {
        this.bookingCollection = bookingCollection;
    }

    @XmlTransient
    public Collection<Passenger> getPassengerCollection() {
        return passengerCollection;
    }

    public void setPassengerCollection(Collection<Passenger> passengerCollection) {
        this.passengerCollection = passengerCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightId != null ? flightId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Flight)) {
            return false;
        }
        Flight other = (Flight) object;
        if ((this.flightId == null && other.flightId != null) || (this.flightId != null && !this.flightId.equals(other.flightId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Flight[ flightId=" + flightId + " ]";
    }
    
}
