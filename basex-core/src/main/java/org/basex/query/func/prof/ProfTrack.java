package org.basex.query.func.prof;

import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.func.*;
import org.basex.query.iter.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.query.value.map.*;
import org.basex.util.*;
import org.basex.util.options.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-19, BSD License
 * @author Christian Gruen
 */
public final class ProfTrack extends StandardFunc {
  /** Profile Options. */
  public static final class TrackOptions extends Options {
    /** Value. */
    public static final BooleanOption VALUE = new BooleanOption("value", true);
    /** Time. */
    public static final BooleanOption TIME = new BooleanOption("time", true);
    /** Memory. */
    public static final BooleanOption MEMORY = new BooleanOption("memory", true);
  }

  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    final TrackOptions opts = toOptions(1, new TrackOptions(), qc);

    // include memory consumption
    long min = -1;
    if(opts.get(TrackOptions.MEMORY)) {
      Performance.gc(4);
      min = Performance.memory();
    }
    // include execution time (called after garbage collection)
    Performance perf = null;
    if(opts.get(TrackOptions.TIME)) {
      perf = new Performance();
    }
    // include resulting value
    Value value = null;
    if(opts.get(TrackOptions.VALUE)) {
      // retrieve and assign value
      value = exprs[0].value(qc);
    } else {
      // iterate through results; skip iteration if iterator is already a value
      final Iter iter = exprs[0].iter(qc);
      if(iter.value() == null) {
        while(qc.next(iter) != null);
      }
    }

    final MapBuilder mb = new MapBuilder();
    // execution time (called before garbage collection)
    if(perf != null) mb.put(TrackOptions.TIME.name(), Dbl.get(Performance.ms(perf.ns(), 1)));
    // memory consumption
    if(min != -1) {
      Performance.gc(2);
      mb.put(TrackOptions.MEMORY.name(), Int.get(Math.max(0, Performance.memory() - min)));
    }
    // evaluated value
    if(value != null) mb.put(TrackOptions.VALUE.name(), value);
    return mb.finish();
  }

  @Override
  protected Expr opt(final CompileContext cc) {
    return adoptType(exprs[0]);
  }
}
