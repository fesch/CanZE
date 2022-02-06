package lu.fisch.canze.activities;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.CarIcon;
import androidx.car.app.model.GridItem;
import androidx.car.app.model.GridTemplate;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.Template;
import androidx.core.graphics.drawable.IconCompat;
import lu.fisch.canze.R;

public class AAScreen extends Screen {
    public AAScreen(CarContext carContext) {
        super(carContext);
    }
    @NonNull
    @Override

    public Template onGetTemplate() {
        CarIcon img1 = new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.aa_battery)).build();
        CarIcon img2 = new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.aa_range)).build();
        CarIcon img3 = new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.aa_consumption)).build();
        CarIcon img4 = new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.aa_energy)).build();
        CarIcon img5 = new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.aa_temp)).build();
        CarIcon img6 = new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), R.drawable.aa_health)).build();

        GridItem item1 = new GridItem.Builder().setLoading(false).setTitle("State of Charge").setText("63,2 %").setImage(img1).build();
        GridItem item2 = new GridItem.Builder().setLoading(false).setTitle("Available Range").setText("143 km").setImage(img2).build();
        GridItem item3 = new GridItem.Builder().setLoading(false).setTitle("Consumption").setText("11 kWh/100 km").setImage(img3).build();
        GridItem item4 = new GridItem.Builder().setLoading(false).setTitle("Available Energy").setText("21 kWh").setImage(img4).build();
        GridItem item5 = new GridItem.Builder().setLoading(false).setTitle("Temperature").setText("2.0°C").setImage(img5).build();
        GridItem item6 = new GridItem.Builder().setLoading(false).setTitle("State of Health").setText("98 %").setImage(img6).build();

        ItemList mylist = new ItemList.Builder()
                .addItem(item1)
                .addItem(item2)
                .addItem(item3)
                .addItem(item4)
                .addItem(item5)
                .addItem(item6)
                .build();

        return new GridTemplate.Builder().setSingleList(mylist).setTitle("CanZE").setLoading(false).build();

    }

}