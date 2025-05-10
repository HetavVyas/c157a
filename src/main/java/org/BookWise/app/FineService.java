package org.BookWise.app;

import java.sql.Date;
import java.util.List;

import org.BookWise.app.DatabaseAccess.Fine;

public class FineService {
    private static final double DAILY_FINE_RATE = 0.50; 

    public static void calculateFines() {
        DatabaseAccess.calculateFines();
    }

    public static List<Fine> getAllFines() {
        return DatabaseAccess.getFines();
    }

    public static List<Fine> getUnpaidFines() {
        return DatabaseAccess.getFines().stream()
                .filter(fine -> "Unpaid".equals(fine.paymentStatus()))
                .toList();
    }

    public static boolean payFine(int fineId) {
        return DatabaseAccess.payFine(fineId);
    }

    public static List<Fine> getFinesForPatron(String userId) {
        return DatabaseAccess.getFinesForPatron(userId);
    }
}