package swift.crdt.operations;

import swift.clocks.Timestamp;
import swift.clocks.TripleTimestamp;
import swift.crdt.RegisterVersioned;
import swift.crdt.interfaces.CRDTOperation;
import swift.crdt.interfaces.Copyable;

public class RegisterUpdate<V extends Copyable> extends BaseOperation<RegisterVersioned<V>> {
    private V val;

    public RegisterUpdate() {
    }

    public RegisterUpdate(TripleTimestamp ts, V val) {
        super(ts);
        this.val = val;
    }

    public V getVal() {
        return this.val;
    }

    @Override
    public void replaceDependentOpTimestamp(Timestamp oldTs, Timestamp newTs) {
        // Insert does not rely on any timestamp dependency.
    }

    @Override
    public void applyTo(RegisterVersioned<V> register) {
        register.update(val, getTimestamp());
    }

    @Override
    public CRDTOperation<RegisterVersioned<V>> withBaseTimestamp(Timestamp ts) {
        return new RegisterUpdate<V>(getTimestamp().withBaseTimestamp(ts), val);
    }
}
