public class Account {
    private int accId;
    private String accName;
    private double balance;
    private String accType;

    public Account(int accId,String accName,double balance,String accType){
        this.accId=accId;   
        this.accName=accName;
        this.balance=balance;
        this.accType=accType;    
    }
    public int getAccId(){return accId;}
    public String getAccName(){return accName;}
    public double getBalance(){return balance;}
    public String getAccType(){return accType;}

    public void deposit(double amount){
        if(amount>0){
             balance+=amount;
        }else{
            System.out.println("invalid amount");
        }
    }
    public boolean withdraw(double amount){

        if(amount>0 && amount<=balance){
            balance-=amount;
            return true;
        }
        return false;
    }
}
