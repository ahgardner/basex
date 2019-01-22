package org.basex.query.func.fn;

import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.func.*;
import org.basex.query.func.file.*;
import org.basex.query.iter.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.query.value.seq.*;
import org.basex.query.value.type.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-19, BSD License
 * @author Christian Gruen
 */
public final class FnTail extends StandardFunc {
  @Override
  public Iter iter(final QueryContext qc) throws QueryException {
    // retrieve and decrement iterator size
    final Iter iter = exprs[0].iter(qc);
    final long size = iter.size();

    // return empty iterator if iterator yields 0 or 1 items, or if result is an empty sequence
    if(size == 0 || size == 1 || iter.next() == null) return Empty.ITER;

    // check if iterator is value-based
    final Value value = iter.value();
    if(value != null) return value.subSequence(1, size - 1, qc).iter();

    // return optimized iterator if result size is known
    if(size > 1) return new Iter() {
      @Override
      public Item next() throws QueryException {
        return qc.next(iter);
      }
      @Override
      public Item get(final long i) throws QueryException {
        return iter.get(i + 1);
      }
      @Override
      public long size() {
        return size - 1;
      }
    };

    // otherwise, return standard iterator
    return new Iter() {
      @Override
      public Item next() throws QueryException {
        return qc.next(iter);
      }
    };
  }

  @Override
  public Value value(final QueryContext qc) throws QueryException {
    // return empty sequence if value has 0 or 1 items
    final Value value = exprs[0].value(qc);
    final long size = value.size();
    return size <= 1 ? Empty.SEQ : value.subSequence(1, size - 1, qc);
  }

  @Override
  protected Expr opt(final CompileContext cc) throws QueryException {
    // ignore standard limitation for large values
    final Expr expr = exprs[0];
    if(expr instanceof Value) return value(cc.qc);

    final long size = expr.size();
    final SeqType st = expr.seqType();
    // zero or one result: return empty sequence
    if(size == 0 || size == 1 || st.zeroOrOne()) return Empty.SEQ;
    // two results: return last item
    if(size == 2) return cc.function(Function._UTIL_LAST, info, expr);

    // rewrite nested function calls
    if(Function.TAIL.is(expr))
      return cc.function(Function.SUBSEQUENCE, info, args(expr)[0], Int.get(3));
    if(Function.SUBSEQUENCE.is(expr)) {
      final SeqRange r = SeqRange.get(expr, cc);
      if(r != null) return cc.function(Function.SUBSEQUENCE, info, args(expr)[0],
          Int.get(r.start + 2), Int.get(r.length - 1));
    }
    if(Function._UTIL_RANGE.is(expr)) {
      final SeqRange r = SeqRange.get(expr, cc);
      if(r != null) return cc.function(Function.SUBSEQUENCE, info, args(expr)[0],
          Int.get(r.start + 2), Int.get(r.length - 1));
    }
    if(Function._FILE_READ_TEXT_LINES.is(expr))
      return FileReadTextLines.opt(this, 1, Long.MAX_VALUE, cc);

    exprType.assign(st.type, Occ.ZERO_MORE, size - 1);
    return this;
  }
}
