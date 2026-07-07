/**
 * The typed data model for raw GSI JSON, rooted at {@link com.cs2gsi.nodes.Node}.
 * <p>
 * Each node (for example {@link com.cs2gsi.nodes.Player},
 * {@link com.cs2gsi.nodes.Round}, or {@link com.cs2gsi.nodes.Bomb}) wraps one
 * section of the game state payload in immutable, strongly-typed fields.
 * Enum types such as {@link com.cs2gsi.nodes.Phase} and
 * {@link com.cs2gsi.nodes.PlayerTeam} fall back to their {@code Undefined}
 * constant when the payload omits or misspells a value.
 */
package com.cs2gsi.nodes;
