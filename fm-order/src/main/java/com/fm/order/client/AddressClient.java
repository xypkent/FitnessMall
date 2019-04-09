package com.fm.order.client;


import com.fm.order.dto.AddressDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class AddressClient {
    public static final List<AddressDTO> addressList = new ArrayList<AddressDTO>(){
        {
            AddressDTO address = new AddressDTO();
            address.setId(1L);
            address.setAddress("中山大道西");
            address.setCity("广州");
            address.setDistrict("天河");
            address.setName("谢宇鹏");
            address.setPhone("13191586852");
            address.setState("广东");
            address.setZipCode("525888");
            address.setIsDefault(true);
            add(address);

            AddressDTO address2 = new AddressDTO();
            address2.setId(2L);
            address.setAddress("珠江东路");
            address.setCity("广州");
            address.setDistrict("海珠");
            address.setName("谢大鹏");
            address.setPhone("13191586852");
            address.setState("广东");
            address.setZipCode("525888");
            address2.setIsDefault(false);
            add(address2);
        }
    };

    public static AddressDTO findById(Long id){
        for (AddressDTO addressDTO : addressList) {
            if(addressDTO.getId() == id) return addressDTO;
        }
        return null;
    }
}

