package eastvillage.dsdragon.controllers;

public class PIDController {

    public float pFactor, iFactor, dFactor;
    private float lastError = 0;
    private float accumError = 0;

    /** A simple PID controller. */
    public PIDController(float pFactor, float iFactor, float dFactor) {
        this.pFactor = pFactor;
        this.iFactor = iFactor;
        this.dFactor = dFactor;
    }

    /** Reset the internal stored last recorded error and the total accumulated error. */
    public void reset() {
        lastError = 0;
        accumError = 0;
    }

    /** Evaluate the output of the PID controller. */
    public float evaluate(float error) {
        return evaluate(error, 0);
    }

    /** Evaluate the output of the PID controller. */
    public float evaluate(float value, float desired) {
        float error = value - desired;
        float change = error - lastError;
        accumError += error;
        lastError = error;
        return pFactor * error + iFactor * accumError + dFactor * change;
    }
}
