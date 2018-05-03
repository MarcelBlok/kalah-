#language: en

Feature: Kalah moves

# ====== Explanation of the board =======
#
#          PLAYER B
#                         pits
#  Scoring pit             |
#          |   /---+---+---+---+---\
#          |   6   5   4   3   2   1
#          v   v   v   v   v   v   v
#
#        | 6 | 3 | 5 | 5 | 2 | 9 | 0 |   |
#        |   | 1 | 5 | 3 | 0 | 8 | 2 | 5 |
#
#              ^   ^   ^   ^   ^   ^   ^
#              1   2   3   4   5   6   |
#              \---+---+---+---+---/   |
#                      |             Scoring pit
#                     pits
#                            PLAYER A

  Scenario: Single move of player A
    Given the following board:
      | 0 | 1 | 1 | 1 | 1 | 1 | 1 |   |
      |   | 1 | 1 | 1 | 1 | 1 | 1 | 0 |
    And it is the turn of player A
    When player A plays from pit 1
    Then the board is:
      | 0 | 1 | 1 | 1 | 1 | 1 | 1 |   |
      |   | 0 | 2 | 1 | 1 | 1 | 1 | 0 |
    And it is player B's turn

  Scenario: Single move of player B
    Given the following board:
      | 0 | 1 | 1 | 1 | 1 | 1 | 1 |   |
      |   | 1 | 1 | 1 | 1 | 1 | 1 | 0 |
    And it is the turn of player B
    When player B plays from pit 1
    Then the board is:
      | 0 | 1 | 1 | 1 | 1 | 2 | 0 |   |
      |   | 1 | 1 | 1 | 1 | 1 | 1 | 0 |
    And it is player A's turn

  Scenario: Multiple stones
    Given the following board:
      | 0 | 1 | 1 | 1 | 1 | 1 | 1 |   |
      |   | 5 | 1 | 1 | 1 | 1 | 1 | 0 |
    And it is the turn of player A
    When player A plays from pit 1
    Then the board is:
      | 0 | 1 | 1 | 1 | 1 | 1 | 1 |   |
      |   | 0 | 2 | 2 | 2 | 2 | 2 | 0 |
    And it is player B's turn

  Scenario: Play ends at opponents pit
    Given the following board:
      | 0 | 1 | 1 | 1 | 1 | 1 | 1 |   |
      |   | 1 | 1 | 1 | 1 | 6 | 1 | 0 |
    And it is the turn of player A
    When player A plays from pit 5
    Then the board is:
      | 0 | 1 | 1 | 2 | 2 | 2 | 2 |   |
      |   | 1 | 1 | 1 | 1 | 0 | 2 | 1 |
    And it is player B's turn

  Scenario: Play ends at own scoring pit; you get another turn
    Given the following board:
      | 0 | 1 | 1 | 1 | 1 | 1 | 1 |   |
      |   | 1 | 1 | 1 | 1 | 1 | 1 | 0 |
    And it is the turn of player A
    When player A plays from pit 6
    Then the board is:
      | 0 | 1 | 1 | 1 | 1 | 1 | 1 |   |
      |   | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
    And it is player A's turn

  Scenario: Around the board; opponent doesn't get a score
    Given the following board:
      | 0 | 1 | 1 | 1 | 1 | 1 | 1 |   |
      |   | 1 | 1 | 1 | 1 | 1 | 8 | 0 |
    And it is the turn of player A
    When player A plays from pit 6
    Then the board is:
      | 0 | 2 | 2 | 2 | 2 | 2 | 2 |   |
      |   | 2 | 1 | 1 | 1 | 1 | 0 | 1 |
    And it is player B's turn

  Scenario: End up in an empty pit; capture opponent's stones and last stone
    Given the following board:
      | 0 | 1 | 1 | 1 | 1 | 5 | 1 |   |
      |   | 1 | 1 | 1 | 1 | 0 | 1 | 0 |
    And it is the turn of player A
    When player A plays from pit 4
    Then the board is:
      | 0 | 1 | 1 | 1 | 1 | 0 | 1 |   |
      |   | 1 | 1 | 1 | 0 | 0 | 1 | 6 |
    And it is player B's turn