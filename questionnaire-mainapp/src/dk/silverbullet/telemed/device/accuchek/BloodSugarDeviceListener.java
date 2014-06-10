package dk.silverbullet.telemed.device.accuchek;

public interface BloodSugarDeviceListener {
    void fetchingDiary();

    void connected();

    void diaryNotFound();

    void tooManyDiariesFound();

    void measurementsParsed(BloodSugarMeasurements measurements);

    void parsingFailed();
}
