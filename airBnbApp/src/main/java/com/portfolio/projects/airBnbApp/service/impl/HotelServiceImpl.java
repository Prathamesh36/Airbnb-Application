package com.portfolio.projects.airBnbApp.service.impl;

import com.portfolio.projects.airBnbApp.dto.HotelDto;
import com.portfolio.projects.airBnbApp.entity.Hotel;
import com.portfolio.projects.airBnbApp.exception.ResourceNotFoundException;
import com.portfolio.projects.airBnbApp.repository.HotelRepository;
import com.portfolio.projects.airBnbApp.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating new hotel with name: {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false); // New hotels are inactive by default
        hotel = hotelRepository.save(hotel);
        log.info("Hotel created with ID: {}", hotel.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Fetching hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));
        log.info("Hotel found: {}", hotel.getName());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        log.info("Hotel updated with ID: {}", hotel.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public void deleteHotelById(Long id) {
        boolean exist = hotelRepository.existsById(id);
        if(!exist) throw new ResourceNotFoundException(("Hotel not found with ID: " + id));

        hotelRepository.deleteById(id);
    }
}
