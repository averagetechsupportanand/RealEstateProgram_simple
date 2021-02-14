import java.util.Scanner;
//peen
public class RealEstateProgram_Simple {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //What is the property name?
        String name = "";
        //What is the list price?
        Double listPrice = 700000.0;

        //-------VACATION HOME SECTION (PUT 100 IF N/A)-------

        //What is the APR you are getting for a 15 year mortgage as a vacation home?
        Double vacationHome15APR = 100.0;
        //What is the APR you are getting for a 30 year mortgage as a vacation home?
        Double vacationHome30APR = 100.0;
        //What is the APR you are getting for a 7/1 ARM as a vacation home
        Double vacationHome7APR = 100.0;

        //-------LLC SECTION (PUT 100 IF N/A)-------

        // What is the APR you are getting for a 15 year mortgage as a LLC?
        Double llc15APR = 4.0;
        //What is the APR you are getting for a 30 year mortgage as a LLC?
        Double llc30APR = 100.0;
        //System.out.println("What is the APR you are getting for a 7/1 ARM as a LLC?
        Double llc7APR = 100.0;

        Double[] APRs = {vacationHome15APR, llc15APR, vacationHome7APR, llc7APR, vacationHome30APR, llc30APR};
        int[] loanLengths = {15,15,20,20,30,30};

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


        System.out.println(name + "Property Numbers:");
        System.out.println();
        for(double price = listPrice; price < listPrice + 100000; price += 1000.0){

            double highestROI = Integer.MIN_VALUE;
            String highestROIString = "";

            for(double downPaymentPercentage = 20; downPaymentPercentage < 101; downPaymentPercentage++){
                for(int index = 0; index < 6; index++){
                    double downPaymentValue = (downPaymentPercentage / 100.0) * price;
                    double financedAmount = price - downPaymentValue;
                    double propertyValue = price;
                    double monthlyMortgage = getMonthlyMortgage(APRs[index],financedAmount, loanLengths[index]);
                    double monthlyInsurance = (0.0036 * price) / 12.0;
                    double monthlyHomeWarranty = (0.0009 * price) / 12.0;
                    double monthlyPropertyTax = (propertyTaxRate * price) / 12.0 / 100.0;
                    double closingCost = price * 0.03;

                    double interestPaid = 0.0;
                    double equityPaid = 0.0;
                    for(int year = 7; year < loanLengths[index]; year++){
                        double appreciationRate = 1.03;
                        propertyValue = propertyValue * appreciationRate;
                        double taxRate = 0.75;

                        for(int month = 0; month < 12; month++){
                            double interestThisMonth = financedAmount * (APRs[index] / 100.0) / 12.0;
                            interestPaid += interestThisMonth;
                            double principleThisMonth = monthlyMortgage - interestThisMonth;
                            equityPaid += principleThisMonth;
                            financedAmount -= principleThisMonth;
                        }
                        double NOIExpenses = (((monthlyInsurance + monthlyHomeWarranty + monthlyPropertyTax + hoa) * 12 * (year))) * taxRate;
                        double costToDate = (taxRate * interestPaid) + NOIExpenses;
                        double totalCashInvested = costToDate + downPaymentValue + (equityPaid) + closingCost;
                        double revenueToDate;
                        if(year < 3){
                            revenueToDate = (anandRent + roommateRent) * 12 * (year);
                        }else{
                            revenueToDate = ((anandRent + roommateRent) * 12 * 3) + (compRent * 11 * (year - 3));
                        }

                        double NOI = revenueToDate - NOIExpenses;
                        double CAP = (NOI / price) * 100;
                        double cashOnCash = ((NOI - monthlyMortgage) / totalCashInvested) * 100;
                        double annualizedCashOnCash = ((Math.pow(1.0+(cashOnCash/100),(1.0/(year)))) - 1.0) * 100;
                        double ROI = ((revenueToDate - costToDate + (propertyValue - price)) / totalCashInvested) * 100;
                        double annualizedROI = ((Math.pow(1.0+(ROI/100),(1.0/(year)))) - 1.0) * 100;
                        if(annualizedROI > highestROI && year > 3){
                            highestROI = annualizedROI;
                            highestROIString =
                                    "the highest annualized ROI is " + annualizedROI + " at $" + price + " happens at " + (year) + " years, with a down payment of " + downPaymentPercentage + "%, the interest paid is " + interestPaid + " the equity paid is " + equityPaid + " it is loan type: " + index + " the total cost to date is " + costToDate + " the revenue to date is " + revenueToDate + " the total cash invested is " + totalCashInvested + " the NOI is " + NOI + " the CAP is " + CAP + " the annualized cash on cash is " + annualizedCashOnCash;
                        }
                    }
                }
            }
            System.out.println(highestROIString);
        }

        System.out.println("done");
    }

    private static double getMonthlyMortgage(double APR, double financedAmount, int loanLength) {
        double monthlyInterest = (APR/100 )/ 12;
        double numberPayment = loanLength * 12;

        double monthlyMortgage = (financedAmount) * ((monthlyInterest * (Math.pow(1+monthlyInterest, numberPayment))) / (Math.pow(1+monthlyInterest, numberPayment)-1));
        return monthlyMortgage;
    }
}
