
package com.springbook.reading.repository;

import com.springbook.reading.ReadingNote;
import com.springbook.reading.NoteType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingNoteRepository
        extends JpaRepository<ReadingNote, Long> {

    List<ReadingNote> findByUserBookId(Long userBookId);

    Optional<ReadingNote> findByIdAndUserBookId(
            Long id,
            Long userBookId
    );

    List<ReadingNote> findByUserBookIdAndNoteType(
            Long userBookId,
            NoteType noteType
    );
}
