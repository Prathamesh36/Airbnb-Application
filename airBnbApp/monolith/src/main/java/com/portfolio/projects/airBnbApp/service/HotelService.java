package com.portfolio.projects.airBnbApp.service;


import com.portfolio.projects.airBnbApp.dto.HotelDto;
import com.portfolio.projects.airBnbApp.dto.HotelInfoDto;
import com.portfolio.projects.airBnbApp.dto.HotelInfoRequestDto;
import com.portfolio.projects.airBnbApp.entity.Hotel;

import java.util.List;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId);

    List<HotelDto> getAllHotels();
}

