import java.util.Scanner;


public class RealEstateProgram_Simple {
    public static final double MONTHS_IN_YEAR = 12.0;
    
    public static void main(String[] args) {
        final double NOT_AVAILABLE = 100.0;
        
        
        String propertyName = "";
        Double propertyListPrice = 700000.0;

        //-------VACATION HOME SECTION (PUT 100 IF N/A)-------

        //What is the APR you are getting for a 15 year mortgage as a vacation home?
        Double vacationHome15APR = NOT_AVAILABLE;
        //What is the APR you are getting for a 30 year mortgage as a vacation home?
        Double vacationHome30APR = NOT_AVAILABLE;
        //What is the APR you are getting for a 7/1 ARM as a vacation home
        Double vacationHome7APR = NOT_AVAILABLE;

        //-------LLC SECTION (PUT 100 IF N/A)-------

        // What is the APR you are getting for a 15 year mortgage as a LLC?
        Double llc15APR = 4.0;
        //What is the APR you are getting for a 30 year mortgage as a LLC?
        Double llc30APR = NOT_AVAILABLE;
        //System.out.println("What is the APR you are getting for a 7/1 ARM as a LLC?
        Double llc7APR = NOT_AVAILABLE;

        Double[] APRs = {vacationHome15APR, llc15APR, vacationHome7APR, llc7APR, vacationHome30APR, llc30APR};
        int[] loanLengths = {15, 15, 20, 20, 30, 30};

        //-------SPECIFICS-------

        //What is the rent Anand would pay (monthly)?
        Double anandRent = 1500.0;
        //What is the rent Anand's roommate would pay (monthly)?
        Double roommateRent = 1500.0;
        //What is the comp rent after Anand leaves (monthly)?
        Double compRent = 4000.0;
        //What is the HOA? (monthly)?
        Double hoa = 0.0;
        //What is the property tax rate? (1.82 is the average in Travis County)?
        Double propertyTaxRate = 1.82;


        System.out.println(propertyName + "Property Numbers:");
        System.out.println();
        for (double price = propertyListPrice; price < propertyListPrice + 100000; price += 1000.0) {

            double highestROI = Integer.MIN_VALUE;
            String highestROIString = "";

            for (double downPaymentPercentage = 20; downPaymentPercentage < 101; downPaymentPercentage++) {
                for (int index = 0; index < APRs.length; index++) {
                    double downPaymentValue = downPaymentPercentage / 100.0 * price;
                    double financedAmount = price - downPaymentValue;
                    double propertyValue = price;
                    double monthlyMortgage = getMonthlyMortgage(APRs[index], financedAmount, loanLengths[index]);
                    double monthlyInsurance = 0.0036 * price / MONTHS_IN_YEAR;
                    double monthlyHomeWarranty = 0.0009 * price / MONTHS_IN_YEAR;
                    double monthlyPropertyTax = propertyTaxRate * price / MONTHS_IN_YEAR / 100.0;
                    double closingCost = price * 0.03;

                    double interestPaid = 0.0;
                    double equityPaid = 0.0;
                    
                    for (int year = 7; year < loanLengths[index]; year++) {
                        double appreciationRate = 1.03;
                        propertyValue = propertyValue * appreciationRate;
                        double taxRate = 0.75;

                        for (int month = 0; month < MONTHS_IN_YEAR; month++) {
                            double interestThisMonth = financedAmount * APRs[index] / 100.0 / MONTHS_IN_YEAR;
                            interestPaid += interestThisMonth;
                            double principleThisMonth = monthlyMortgage - interestThisMonth;
                            equityPaid += principleThisMonth;
                            financedAmount -= principleThisMonth;
                        }
                        
                        double NOIExpenses = (monthlyInsurance + monthlyHomeWarranty + monthlyPropertyTax + hoa) * MONTHS_IN_YEAR * year * taxRate;
                        double costToDate = taxRate * interestPaid + NOIExpenses;
                        double totalCashInvested = costToDate + downPaymentValue + equityPaid + closingCost;
                        double revenueToDate;
                        
                        if (year < 3) {
                            revenueToDate = (anandRent + roommateRent) * MONTHS_IN_YEAR * year;
                        } else {
                            revenueToDate = (anandRent + roommateRent) * MONTHS_IN_YEAR * 3 + compRent * 11 * (year - 3);
                        }

                        double NOI = revenueToDate - NOIExpenses;
                        double CAP = NOI / price * 100;
                        double cashOnCash = (NOI - monthlyMortgage) / totalCashInvested * 100;
                        double annualizedCashOnCash = (Math.pow(1.0 + cashOnCash / 100, 1.0 / year) - 1.0) * 100;
                        double ROI = (revenueToDate - costToDate + propertyValue - price) / totalCashInvested * 100;
                        double annualizedROI = (Math.pow(1.0 + ROI / 100, 1.0 / year) - 1.0) * 100;
                        if (annualizedROI > highestROI && year > 3) {
                            highestROI = annualizedROI;
                            highestROIString = String.format("The highest annualized ROI is %.2f "
                                                             + "at $%.2f.\n", annualizedROI, price);
                            highestROIString += String.format("Happens at %d years with a down"
                                                              + "payment of %.2f%%.\n", year, downPaymentPercentage);
                            
                            highestROIString += String.format("The interest paid is " + interestPaid + ".\n");
                            highestROIString += String.format("The equity paid is %.2f.\n", equityPaid);
                            highestROIString += "It is loan type: " + index + ".\n";
                            highestROIString += String.format("The total cost to date is %.2f.\n", costToDate);
                            highestROIString += String.format("The revenue to date is %.2f.\n", revenueToDate);
                            highestROIString += String.format("The total cash invested is %.2f.\n", totalCashInvested);
                            highestROIString += String.format("The NOI is %.2f.\n", NOI);
                            highestROIString += String.format("The CAP is %.2f.\n", CAP);
                            highestROIString += String.format("The annualized cash on cash is %.2f.\n", annualizedCashOnCash);
                        }
                    }
                }
            }
            System.out.println(highestROIString);
            System.out.println();
        }

        System.out.println("Done");
    }

    private static double getMonthlyMortgage(double APR, double financedAmount, int loanLength) {
        double monthlyInterest = APR / 100  / MONTHS_IN_YEAR;
        double numberPayment = loanLength * MONTHS_IN_YEAR;

        double monthlyMortgage = financedAmount * monthlyInterest * Math.pow(1 + monthlyInterest, numberPayment) / (Math.pow(1 + monthlyInterest, numberPayment) - 1);
        return monthlyMortgage;
    }
}