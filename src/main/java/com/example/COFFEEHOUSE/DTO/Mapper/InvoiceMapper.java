package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Response.InvoiceItemResp;
import com.example.COFFEEHOUSE.DTO.Response.InvoiceResp;
import com.example.COFFEEHOUSE.Entity.OrderEntity;
import com.example.COFFEEHOUSE.Entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "storeName", ignore = true)
    @Mapping(target = "voucherDiscount", ignore = true)
    @Mapping(target = "voucherCode", ignore = true)
    @Mapping(target = "shippingFee", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "items", ignore = true)
    InvoiceResp toInvoiceResp(OrderEntity entity);

    @Mapping(target = "orderItemId", source = "id")
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "sizeName", ignore = true)
    @Mapping(target = "unitPrice", source = "priceAtPurchase")
    @Mapping(target = "lineTotal", expression = "java(entity.getPriceAtPurchase() * entity.getQuantity())")
    InvoiceItemResp toInvoiceItemResp(OrderItemEntity entity);
}

