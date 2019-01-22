package org.basex.query.func.repo;

import org.basex.query.*;
import org.basex.query.util.pkg.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-19, BSD License
 * @author Christian Gruen
 */
public final class RepoDelete extends RepoFn {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    checkCreate(qc);
    new RepoManager(qc.context, info).delete(Token.string(toToken(exprs[0], qc)));
    return null;
  }
}
