package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import java.util.List;
import java.util.UUID;

public record LectureProductReserveData(
    List<UUID> productIds, UUID userId, UserRole userRole, String userName) {}
