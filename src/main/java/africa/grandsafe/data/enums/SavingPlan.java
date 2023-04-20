package africa.grandsafe.data.enums;

import africa.grandsafe.exceptions.GenericException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum SavingPlan {
    DAILY_SAVINGS,
    WEEKLY_SAVING,
    MONTHLY_SAVINGS,
    NONE;

    public static SavingPlan getPlan(String savingPlan){
        List<SavingPlan> savingPlanList = Arrays.asList(
                DAILY_SAVINGS,
                WEEKLY_SAVING,
                MONTHLY_SAVINGS,
                NONE);
        for (SavingPlan plan : savingPlanList) {
            if(Objects.equals(plan.toString(), savingPlan.toUpperCase())) return plan;
        }
        throw new GenericException("Plan does not Exist");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
