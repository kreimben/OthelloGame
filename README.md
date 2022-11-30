# OthelloGame
Othello PvP (2-Player) Game Application. This repository contains a Windows application, created in Java using Swing and AWT, for playing the Othello game, a simple game that played on an 8 by 8 checkered board with 64 double-sided black and white discs. The game is easy to learn, but it takes time to master and develop winning strategies.

-----------------------------------------------------------------------------------------------
## protocol

* 100 좌포를 이용해 플레이 함 request. c -> s
* 101 서버로부터 메세지를 받음 response. s -> c
* 102 좌표를 이용해 플레이함 response. s -> c
* 104 채팅 request. c -> s
* ~~200 방을 만듬 request. c -> s~~
* ~~201 방이 만들어짐 response. s -> c~~
* 202 방에 입장 request. c -> s
* 203 접속 종료 request. c -> s
* 204 방에 입장 response. s -> c
* 301 히스토리 요청 request. c -> s
* 302 히스토리 응답 response. s -> c

-----------------------------------------------------------------------------------------------

Screenshot from the Application:
-----------------------------------------------------------------------------------------------
![Screenshot-Othello-Game](https://user-images.githubusercontent.com/76199286/152112877-cfcc2ad2-0ee4-43c4-aba8-25820666d244.png)

-----------------------------------------------------------------------------------------------

Rules Of The Game (Source - https://www.eothello.com/#how-to-play):
-----------------------------------------------------------------------------------------------

Othello is a strategy board game for two players (Black and White), played on an 8 by 8 board. The game traditionally begins with four discs placed in the middle of the board as shown below. Black moves first.

![image](https://user-images.githubusercontent.com/76199286/152113198-26ea6b71-5832-4e7e-be53-2dd0812c63da.png)

Black must place a black disc on the board, in such a way that there is at least one straight (horizontal, vertical, or diagonal) occupied line between the new disc and another black disc, with one or more contiguous white pieces between them. In the starting position, Black has the following 4 options indicated by translucent discs:

![image](https://user-images.githubusercontent.com/76199286/152113234-9dfefe42-22b5-4303-93af-447013b46550.png)

After placing the disc, Black flips all white discs lying on a straight line between the new disc and any existing black discs. All flipped discs are now black. If Black decides to place a disc in the topmost location, one white disc gets flipped, and the board now looks like this:

![image](https://user-images.githubusercontent.com/76199286/152113257-c1beb9db-536e-4f40-874c-cab4291c6bef.png)

Now White plays. This player operates under the same rules, with the roles reversed: White lays down a white disc, causing black discs to flip. Possibilities at this time would be:

![image](https://user-images.githubusercontent.com/76199286/152113275-7c9f8b13-fc62-477b-b488-74018f6ae443.png)

If White plays the bottom left option and flips one disc:

![image](https://user-images.githubusercontent.com/76199286/152113283-e2d3e36b-5a8b-4872-bfbf-812d926ce09c.png)

Players alternate taking turns. If a player does not have any valid moves, play passes back to the other player. When neither player can move, the game ends. A game of Othello may end before the board is completely filled.

The player with the most discs on the board at the end of the game wins. If both players have the same number of discs, then the game is a draw.

-----------------------------------------------------------------------------------------------
