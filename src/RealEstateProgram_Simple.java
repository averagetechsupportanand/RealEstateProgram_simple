import java.util.Scanner;


public class RealEstateProgram_Simple {
    public static final double MONTHS_IN_YEAR = 12.0;
    
    public static void main(String[] args) {
        final double NOT_AVAILABLE = 100.0;

        // Scanner getName = new Scanner(System.in);
        // System.out.println("Enter property name: ");
        // getName.nextLine();
        
        String propertyName = "";
        Double propertyListPrice = 600000.0;

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

        Double[] APRs = {vacationHome15APR, llc15APR, vacationHome7APR,
                         llc7APR, vacationHome30APR, llc30APR};
        int[] loanLengths = {15, 15, 20, 20, 30, 30};

        //-------SPECIFICS-------

        //What is the rent Anand would pay (monthly)?
        Double anandRent = 1500.0;
        //What is the rent Anand's roommate would pay (monthly)?
        Double roommateRent = 1500.0;
        //What is the comp rent after Anand leaves (monthly)?
        Double compRent = 3500.0;
        //What is the HOA? (monthly)?
        Double hoa = 150.0;
        //What is the property tax rate? (1.82 is the average in Travis County)?
        Double propertyTaxRate = 0.0182;


        System.out.println(propertyName + "Property Numbers:");
        System.out.println();

        for (double price = propertyListPrice; price < propertyListPrice + 100_000; price += 1_000.0) {

            double highestROI = Integer.MIN_VALUE;
            String highestROIString = "";

            for (double downPaymentPercentage = 0.2; downPaymentPercentage <= 1.0; downPaymentPercentage += 0.01) {
                for (int index = 0; index < APRs.length; index++) {

                    double downPaymentValue = downPaymentPercentage * price;
                    double financedAmount = price - downPaymentValue;
                    double propertyValue = price;
                    double monthlyMortgage = getMonthlyMortgage(APRs[index], financedAmount, loanLengths[index]);
                    double monthlyInsurance = price / MONTHS_IN_YEAR * 0.0036;
                    double monthlyHomeWarranty = price / MONTHS_IN_YEAR * 0.0015;
                    double monthlyPropertyTax = price / MONTHS_IN_YEAR * propertyTaxRate;
                    double closingCost = price * 0.03;

                    double interestPaid = 0.0;
                    double equityPaid = 0.0;

                    // 3 is simplification b/c it assumes someone will be living there for 3 yrs
                    // won't be 3 later
                    for (int year = 0; year < loanLengths[index]; year++) {
                        double appreciationRate = 1.04;
                        double taxRate = 0.35;
                        
                        propertyValue = propertyValue * appreciationRate;

                        for (int month = 0; month < MONTHS_IN_YEAR; month++) {
                            double interestThisMonth = APRs[index] / 100.0 / MONTHS_IN_YEAR * financedAmount ;
                            double principleThisMonth = monthlyMortgage - interestThisMonth;

                            interestPaid += interestThisMonth;
                            equityPaid += principleThisMonth;
                            financedAmount -= principleThisMonth;
                        }

                        // TODO look at equation
                        double NOIExpenses = (monthlyInsurance + monthlyHomeWarranty + monthlyPropertyTax + hoa)
                                             * MONTHS_IN_YEAR * year;
                                             
        
                        double costToDate = (interestPaid + NOIExpenses) - (interestPaid + NOIExpenses) * taxRate;
                        double totalCashInvested = costToDate + downPaymentValue + equityPaid + closingCost;
                        // deal with taxes when calculating revenueDate
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
                        double cashGeneratedAtSale = propertyValue * 0.94;
                        double ROI = (revenueToDate - costToDate + cashGeneratedAtSale - price) / totalCashInvested * 100;
                        double annualizedROI = (Math.pow(1.0 + ROI / 100, 1.0 / year) - 1.0) * 100;

                        if (annualizedROI > highestROI && year >= 3) {
                            highestROI = annualizedROI;
                            highestROIString = getResults(annualizedROI, price, year,
                                                          downPaymentPercentage, interestPaid,
                                                          equityPaid, index, costToDate,
                                                          revenueToDate, totalCashInvested, NOI,
                                                          CAP, annualizedCashOnCash);
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
        double monthlyInterestRate = APR / 100  / MONTHS_IN_YEAR;
        double numberOfPayment = loanLength * MONTHS_IN_YEAR;

        double monthlyMortgage = financedAmount * monthlyInterestRate
                                 * Math.pow(1 + monthlyInterestRate, numberOfPayment)
                                 / (Math.pow(1 + monthlyInterestRate, numberOfPayment) - 1);
        return monthlyMortgage;
    }

    private static String getResults(double annualizedROI, double price, int year,
                                     double downPaymentPercentage, double interestPaid,
                                     double equityPaid, int index, double costToDate,
                                     double revenueToDate, double totalCashInvested,
                                     double NOI, double CAP, double annualizedCashOnCash) {
        String highestROIString = String.format("The highest annualized ROI is %.2f "
                + "at $%.2f.\n", annualizedROI, price);
        highestROIString += String.format("Happens at %d years with a down"
                + "payment of %.2f%%.\n", year, downPaymentPercentage * 100);

        highestROIString += String.format("The interest paid is %.2f.\n", interestPaid);
        highestROIString += String.format("The equity paid is %.2f.\n", equityPaid);
        highestROIString += "It is loan type: " + index + ".\n";
        highestROIString += String.format("The total cost to date is %.2f.\n", costToDate);
        highestROIString += String.format("The revenue to date is %.2f.\n", revenueToDate);
        highestROIString += String.format("The total cash invested is %.2f.\n", totalCashInvested);
        highestROIString += String.format("The NOI is %.2f.\n", NOI);
        highestROIString += String.format("The CAP is %.2f.\n", CAP);
        highestROIString += String.format("The annualized cash on cash is %.2f.\n", annualizedCashOnCash);

        return highestROIString;
    }
}