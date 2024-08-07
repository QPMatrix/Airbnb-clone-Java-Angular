package tech.qpmatrix.airbnbclone.listing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.qpmatrix.airbnbclone.listing.domain.ListingPicture;

public interface ListingPictureRepository extends JpaRepository<ListingPicture,Long> {
}
