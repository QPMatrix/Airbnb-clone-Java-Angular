package tech.qpmatrix.airbnbclone.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.qpmatrix.airbnbclone.booking.domain.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
