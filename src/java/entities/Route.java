/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author james
 */
@Entity
@Table(name = "route")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Route.findAll", query = "SELECT r FROM Route r")
    , @NamedQuery(name = "Route.findByRouteId", query = "SELECT r FROM Route r WHERE r.routeId = :routeId")
    , @NamedQuery(name = "Route.findByRouteName", query = "SELECT r FROM Route r WHERE r.routeName = :routeName")
    , @NamedQuery(name = "Route.findByAirportFrom", query = "SELECT r FROM Route r WHERE r.airportFrom = :airportFrom")
    , @NamedQuery(name = "Route.findByAirportTo", query = "SELECT r FROM Route r WHERE r.airportTo = :airportTo")
    , @NamedQuery(name = "Route.findByAdultFare", query = "SELECT r FROM Route r WHERE r.adultFare = :adultFare")
    , @NamedQuery(name = "Route.findByChildFare", query = "SELECT r FROM Route r WHERE r.childFare = :childFare")
    , @NamedQuery(name = "Route.findByInfantFare", query = "SELECT r FROM Route r WHERE r.infantFare = :infantFare")})
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "RouteId")
    private Integer routeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "RouteName")
    private String routeName;
    @Basic(optional = false)
    @NotNull
//    @Size(min = 1, max = 45)
    @Column(name = "AirportFrom")
    private AirportEnum airportFrom;
    @Basic(optional = false)
    @NotNull
//    @Size(min = 1, max = 45)
    @Column(name = "AirportTo")
    private AirportEnum airportTo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AdultFare")
    private double adultFare;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ChildFare")
    private double childFare;
    @Basic(optional = false)
    @NotNull
    @Column(name = "InfantFare")
    private double infantFare;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "routeId")
    private Collection<Flight> flightCollection;

    public Route() {
    }

    public Route(Integer routeId) {
        this.routeId = routeId;
    }

    public Route(Integer routeId, String routeName, AirportEnum airportFrom, AirportEnum airportTo, double adultFare, double childFare, double infantFare) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.airportFrom = airportFrom;
        this.airportTo = airportTo;
        this.adultFare = adultFare;
        this.childFare = childFare;
        this.infantFare = infantFare;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }



    public void setAdultFare(long adultFare) {
        this.adultFare = adultFare;
    }

    public AirportEnum getAirportFrom() {
        return airportFrom;
    }

    public void setAirportFrom(AirportEnum airportFrom) {
        this.airportFrom = airportFrom;
    }

    public AirportEnum getAirportTo() {
        return airportTo;
    }

    public void setAirportTo(AirportEnum airportTo) {
        this.airportTo = airportTo;
    }

    public double getAdultFare() {
        return adultFare;
    }

    public void setAdultFare(double adultFare) {
        this.adultFare = adultFare;
    }

    public double getChildFare() {
        return childFare;
    }

    public void setChildFare(double childFare) {
        this.childFare = childFare;
    }

    public double getInfantFare() {
        return infantFare;
    }

    public void setInfantFare(double infantFare) {
        this.infantFare = infantFare;
    }



    public void setChildFare(long childFare) {
        this.childFare = childFare;
    }



    public void setInfantFare(long infantFare) {
        this.infantFare = infantFare;
    }

    @XmlTransient
    public Collection<Flight> getFlightCollection() {
        return flightCollection;
    }

    public void setFlightCollection(Collection<Flight> flightCollection) {
        this.flightCollection = flightCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (routeId != null ? routeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Route)) {
            return false;
        }
        Route other = (Route) object;
        if ((this.routeId == null && other.routeId != null) || (this.routeId != null && !this.routeId.equals(other.routeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Route[ routeId=" + routeId + " ]";
    }
    
}
