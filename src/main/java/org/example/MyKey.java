package org.example;

import java.util.Objects;

public class MyKey {
    String companyName;
    String accountNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyKey)) return false;
        MyKey myKey = (MyKey) o;
        return Objects.equals(companyName, myKey.companyName) && Objects.equals(accountNo, myKey.accountNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, accountNo);
    }

    //    String corpIdCompCode;

}
