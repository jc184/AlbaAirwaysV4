/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf;

import entities.AirportEnum;
import entities.PassengerTypeEnum;
import entities.SeatTypeEnum;
import entities.TicketTypeEnum;
import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author james
 */
@ManagedBean
@ApplicationScoped
@Named
public class Data {

    public AirportEnum[] getAirports() {
        return AirportEnum.values();
    }
    
    public SeatTypeEnum[] getSeatTypes() {
        return SeatTypeEnum.values();
    }
    
    public TicketTypeEnum[] getTicketTypes() {
        return TicketTypeEnum.values();
    }
    
    public PassengerTypeEnum[] getPassengerTypes() {
        return PassengerTypeEnum.values();
    }

}


