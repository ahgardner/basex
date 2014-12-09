package org.basex.query.up.primitives;

import static org.basex.query.QueryError.*;

import org.basex.core.users.*;
import org.basex.query.*;
import org.basex.util.*;
import org.basex.util.list.*;

/**
 * Update that operates on a global user.
 *
 * @author BaseX Team 2005-14, BSD License
 * @author Christian Gruen
 */
public abstract class UserUpdate extends Update {
  /** Users. */
  protected final Users users;
  /** User. */
  protected final User user;
  /** Databases. */
  protected final StringList databases = new StringList();

  /**
   * Constructor.
   * @param type type of this operation
   * @param user user
   * @param qc query context
   * @param db database
   * @param info input info
   */
  public UserUpdate(final UpdateType type, final User user, final String db, final QueryContext qc,
      final InputInfo info) {
    super(type, info);
    this.user = user;
    users = qc.context.users;
    databases.add(db);
  }

  /**
   * Returns the name of the database.
   * @return name
   */
  public String name() {
    return user.name();
  }

  /**
   * Applies this operation.
   * @throws QueryException exception
   */
  public abstract void apply() throws QueryException;

  /**
   * Returns an info string.
   * @return info string
   */
  protected abstract String operation();

  @Override
  public final int size() {
    return 1;
  }

  @Override
  public final void merge(final Update update) throws QueryException {
    final String db = ((UserUpdate) update).databases.get(0);
    if(databases.contains(db)) throw BXUS_ONCE_X_X.get(info, name(), operation());
    databases.add(db);
  }
}
