package com.food.ordering.system.order.service.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class StreetAddress
{
    private final UUID uuid;
    private final String street;
    private final String postalCode;
    private final String city;


    public StreetAddress(UUID uuid, String street, String postalCode, String city)
    {
        this.uuid = uuid;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
    }


    /**
     * UUID id not required for address equality, but used for tracking in DataLayer.
     *
     * @param o another object
     * @return true if street postalCode and city are the same
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreetAddress that = (StreetAddress) o;
        return Objects.equals(street, that.street) && Objects.equals(postalCode, that.postalCode) && Objects.equals(city,
                that.city);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(street, postalCode, city);
    }

    public UUID getId()
    {
        return uuid;
    }

    public String getStreet()
    {
        return street;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public String getCity()
    {
        return city;
    }


}
