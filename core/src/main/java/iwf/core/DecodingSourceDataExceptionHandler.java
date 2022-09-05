package iwf.core;

public interface DecodingSourceDataExceptionHandler{
    /**
     * The output type can be changed which causes data cannot be decoded to the new type.
     * Implement this handler is to fix this in-compatible change so that the data can still be decoded to the old type.
     * NOTE: workflow input tyoe is a output type of STARTED_STATE
     *
     * @param e             the exception when try to decode sourceData into the output type of sourceState of sourceStateId
     * @param sourceData    the data to be decoded into the output of the source state, which is a previous state(special case, workflow input is the output of STARTED_STATE)
     * @param sourceStateId the stateId of the source state
     * @return the object that should be decoded correctly
     */
    Object handleDecodingSourceDataException(Exception e, byte[] sourceData, String sourceStateId);
}