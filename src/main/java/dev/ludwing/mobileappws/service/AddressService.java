package dev.ludwing.mobileappws.service;

import java.util.List;

import dev.ludwing.mobileappws.shared.dto.AddressDto;

public interface AddressService {
	List<AddressDto> getAddresses(String userId);
}
