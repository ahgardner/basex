package org.basex.query.func.fn;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-19, BSD License
 * @author Christian Gruen
 */
public final class FnGenerateId extends ContextFn {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    final ANode node = toEmptyNode(ctxArg(0, qc), qc);
    if(node == null) return Str.ZERO;

    final TokenBuilder tb = new TokenBuilder(Token.ID);
    if(node instanceof DBNode) {
      final DBNode dbnode = (DBNode) node;
      tb.addInt(dbnode.data().dbid).add('d').addInt(dbnode.pre());
    } else {
      tb.addInt(node.id);
    }
    return Str.get(tb.finish());
  }
}
