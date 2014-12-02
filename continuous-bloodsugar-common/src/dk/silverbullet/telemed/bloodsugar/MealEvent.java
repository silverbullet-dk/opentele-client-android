package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * User entered Food entry
 */
public class MealEvent extends Event {

    @Expose
    public final String eventType = "MealEvent";

    public enum FoodType {
        BREAKFAST,
        LUNCH,
        DINNER,
        SNACK,
        UNKNOWN
    }

    @Expose
    public FoodType foodType;

    @Expose
    public String carboGrams;
}
