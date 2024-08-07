package tech.qpmatrix.airbnbclone.listing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.qpmatrix.airbnbclone.listing.domain.Listing;

public interface ListingRepository  extends JpaRepository<Listing, Long> {

}
