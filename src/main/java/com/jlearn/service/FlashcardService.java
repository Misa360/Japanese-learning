package com.jlearn.service;

import com.jlearn.dao.FlashcardDao;
import com.jlearn.dao.FlashcardDeckDao;
import com.jlearn.dao.FlashcardReviewDao;
import com.jlearn.exception.NotFoundException;
import com.jlearn.exception.ValidationException;
import com.jlearn.model.Flashcard;
import com.jlearn.model.FlashcardDeck;
import com.jlearn.model.FlashcardReview;
import com.jlearn.util.DbUtil;

import java.util.List;

/** Nghiep vu xem bo the, tu tao bo rieng, hoc the den han va cap nhat lich SRS. */
public class FlashcardService {
    private final FlashcardDeckDao deckDao = new FlashcardDeckDao();
    private final FlashcardDao flashcardDao = new FlashcardDao();
    private final FlashcardReviewDao reviewDao = new FlashcardReviewDao();

    public List<FlashcardDeck> decks(long userId, String search) {
        return DbUtil.read(c -> deckDao.findAvailable(c, userId, search));
    }

    public FlashcardDeck studyDeck(long userId, long deckId) {
        return DbUtil.read(c -> {
            FlashcardDeck deck = deckDao.findAvailableById(c, userId, deckId);
            if (deck == null) throw new NotFoundException("Không tìm thấy bộ flashcard");
            deck.setCardCount(flashcardDao.countByDeck(c, deckId));
            deck.setCards(flashcardDao.findDueByDeck(c, userId, deckId));
            deck.setRecentReviews(reviewDao.recentByDeck(c, userId, deckId, 8));
            return deck;
        });
    }

    public long createDeck(long userId, String title, String description) {
        String cleanTitle = title == null ? "" : title.trim();
        String cleanDescription = description == null ? "" : description.trim();
        if (cleanTitle.isEmpty() || cleanTitle.length() > 200) {
            throw new ValidationException("Tên bộ thẻ cần từ 1 đến 200 ký tự");
        }
        if (cleanDescription.length() > 4000) throw new ValidationException("Mô tả bộ thẻ tối đa 4000 ký tự");
        return DbUtil.tx(c -> deckDao.create(c, userId, cleanTitle, cleanDescription));
    }

    public long addCard(long userId, long deckId, String front, String back, String example) {
        String cleanFront = front == null ? "" : front.trim();
        String cleanBack = back == null ? "" : back.trim();
        String cleanExample = example == null ? "" : example.trim();
        if (cleanFront.isEmpty() || cleanFront.length() > 10000) {
            throw new ValidationException("Mặt trước cần từ 1 đến 10000 ký tự");
        }
        if (cleanBack.isEmpty() || cleanBack.length() > 10000) {
            throw new ValidationException("Mặt sau cần từ 1 đến 10000 ký tự");
        }
        if (cleanExample.length() > 10000) throw new ValidationException("Ví dụ tối đa 10000 ký tự");
        return DbUtil.tx(c -> {
            if (deckDao.findOwned(c, userId, deckId) == null) {
                throw new NotFoundException("Chỉ chủ bộ thẻ mới được thêm flashcard");
            }
            int order = deckDao.nextSortOrder(c, deckId);
            return flashcardDao.create(c, deckId, cleanFront, cleanBack, cleanExample, order);
        });
    }

    /** rating: 1=Học lại, 2=Khó, 3=Tốt, 4=Dễ. */
    public int review(long userId, long deckId, long cardId, int rating) {
        if (rating < 1 || rating > 4) throw new ValidationException("Mức đánh giá không hợp lệ");
        return DbUtil.tx(c -> {
            if (deckDao.findAvailableById(c, userId, deckId) == null) {
                throw new NotFoundException("Không tìm thấy bộ flashcard");
            }
            Flashcard card = flashcardDao.findById(c, userId, cardId);
            if (card == null || card.getDeckId() != deckId) throw new NotFoundException("Không tìm thấy flashcard");
            if (!flashcardDao.isDue(c, userId, cardId)) throw new ValidationException("Thẻ này chưa đến hạn ôn tập");

            int repetitions = card.getRepetitionCount();
            int previousInterval = card.getIntervalDays();
            double ease = card.getEaseFactor();
            int interval;
            if (rating == 1) {
                repetitions = 0;
                interval = 0; // Den han ngay, de hoc vien co the thu lai ngay.
                ease = Math.max(1.3, ease - 0.20);
            } else {
                if (rating == 2) {
                    interval = repetitions == 0 ? 1 : Math.max(1, (int) Math.ceil(Math.max(1, previousInterval) * 1.2));
                    ease = Math.max(1.3, ease - 0.15);
                } else if (rating == 3) {
                    interval = repetitions == 0 ? 1 : (repetitions == 1 ? 6 : Math.max(1, (int) Math.round(previousInterval * ease)));
                } else {
                    interval = repetitions == 0 ? 4 : Math.max(1, (int) Math.round(Math.max(1, previousInterval) * ease * 1.3));
                    ease += 0.15;
                }
                repetitions++;
            }
            reviewDao.saveState(c, userId, cardId, repetitions, interval, ease);
            reviewDao.insert(c, userId, cardId, rating, interval, ease);
            return interval;
        });
    }
}
