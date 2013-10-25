package dk.silverbullet.telemed.device.accuchek;

public interface AccuChekListener {
    void fetchingDiary();

    void connected();

    void diaryNotFound();

    void tooManyDiariesFound();

    void measurementsParsed(BloodSugarMeasurements measurements);

    void parsingFailed();
}
