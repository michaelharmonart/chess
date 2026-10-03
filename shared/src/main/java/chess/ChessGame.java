package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard gameBoard = new ChessBoard();
    private TeamColor currentTeam = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        HashSet<ChessPosition> opposingPieceAttackPostions = new HashSet<ChessPosition>();
        ChessBoard.BoardSize size = gameBoard.getBoardSize();
        ChessPosition kingPosition = null;
        for (int row = 1; row < size.rows() + 1; row++) {
            for (int col = 1; col < size.columns() + 1; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece == null) {
                    continue;
                }
                TeamColor pieceColor = piece.getTeamColor();
                if (pieceColor == teamColor) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPosition = position;
                    }
                } else {
                    for (ChessMove move : piece.pieceMoves(gameBoard, position)) {
                        opposingPieceAttackPostions.add(move.getEndPosition());
                    }
                }
            }
        }
        if (kingPosition == null) {
            return false;
        }
        return opposingPieceAttackPostions.contains(kingPosition);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
