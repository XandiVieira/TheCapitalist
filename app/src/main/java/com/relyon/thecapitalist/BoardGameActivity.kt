package com.relyon.thecapitalist

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.relyon.thecapitalist.enums.BoardRegion
import com.relyon.thecapitalist.model.BoardPosition
import com.relyon.thecapitalist.model.Coordinate
import com.relyon.thecapitalist.model.Match
import com.relyon.thecapitalist.model.Player
import com.relyon.thecapitalist.util.Constants
import com.relyon.thecapitalist.util.Util
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BoardGameActivity : AppCompatActivity(), ChangePosition {

    private lateinit var boardPositions: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var boardLayout: FrameLayout
    private var imageView: ArrayList<ImageView>? = null
    private var screenHeight: Int = 0
    private var screenWidth: Int = 0
    private lateinit var match: Match
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_game)

        boardLayout = findViewById(R.id.board_layout)
        imageView = ArrayList()
        imageView?.add(0, ImageView(this))
        imageView?.add(1, ImageView(this))

        if (Util.user?.uid == "ztr92NNX0fX7NIEaj1OFaHUIBFx1") {
            createMatch()
        } else {
            findMatch()
        }

        gridLayoutManager = GridLayoutManager(this, 5)
        boardPositions = findViewById(R.id.board_positions)
        boardPositions.layoutManager = gridLayoutManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels

        val boardPositionAdapter = BoardPositionRecyclerView(
            screenWidth / 5,
            screenHeight / 7,
            this
        )
        boardPositions.adapter = boardPositionAdapter
        boardPositionAdapter.setColorList(generateData())
    }

    private fun movePlayerListener() {
        Util.db.child(Constants.DATABASE_REF_MATCH).child(match.uid)
            .child(Constants.DATABASE_REF_PLAYER)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val player = snapshot.getValue(Player::class.java)
                    if (player?.coordinates != null) {
                        drawPawn(player)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val player = snapshot.getValue(Player::class.java)
                    if (player?.coordinates != null) {
                        goTo(player.coordinates!!, player.number)
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    return
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    return
                }

                override fun onCancelled(error: DatabaseError) {
                    return
                }

            })
    }

    private fun drawPawn(player: Player?) {
        //imageView!![player!!.number]
        imageView!![player?.number!!].layoutParams = FrameLayout.LayoutParams(50, 98)
        if (player.number == 0) {
            imageView!![0].setImageResource(R.drawable.greenpawn)
        } else {
            imageView!![1].setImageResource(R.drawable.redpawn)
        }

        boardLayout.addView(imageView!![player.number], boardLayout.size)
    }

    private fun findMatch() {
        Util.db.child(Constants.DATABASE_REF_MATCH).child("151c63e8-5aaa-4fca-ac0c-98d3c2e1233b")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Util.db.child(Constants.DATABASE_REF_MATCH)
                        .child("151c63e8-5aaa-4fca-ac0c-98d3c2e1233b").removeEventListener(this)
                    match = snapshot.getValue(Match::class.java)!!
                    createPlayer()
                    movePlayerListener()
                }

                override fun onCancelled(error: DatabaseError) {
                    return
                }
            })
    }

    private fun createMatch() {
        createPlayer()
        match = Match(
            UUID.randomUUID().toString(), listOf(
                player
            ), 500f
        )
        Util.db.child(Constants.DATABASE_REF_MATCH).child(match.uid).setValue(match)
        movePlayerListener()
    }

    private fun createPlayer(): Player {
        val coordinates = HashMap<String, Float>()
        coordinates["x"] = 0f
        coordinates["y"] = 0f
        val playerNumber =
            if (Util.user?.uid == "ztr92NNX0fX7NIEaj1OFaHUIBFx1") 0 else 1
        val color = if (Util.user?.uid == "ztr92NNX0fX7NIEaj1OFaHUIBFx1") Color.RED else Color.GREEN
        player = Player(
            Util.user?.uid.toString(),
            Util.user?.nickname.toString(),
            Coordinate(
                screenWidth / 5,
                screenHeight / 7,
                0f,
                0f,
                BoardPosition("place 1", BoardRegion.TOP, Color.DKGRAY)
            ),
            playerNumber,
            color
        )
        if (Util.user?.uid != "ztr92NNX0fX7NIEaj1OFaHUIBFx1") {
            Util.db.child(Constants.DATABASE_REF_MATCH).child(match.uid)
                .child(Constants.DATABASE_REF_PLAYER).child(1.toString()).setValue(player)
        }
        return player
    }

    private fun generateData(): List<BoardPosition> {
        val listOfBoardPositions = mutableListOf<BoardPosition>()

        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.TOP, Color.DKGRAY))
        listOfBoardPositions.add(BoardPosition("place 2", BoardRegion.TOP, Color.CYAN))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.TOP, Color.BLACK))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.TOP, Color.BLUE))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.TOP, Color.GRAY))

        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.LEFT, Color.GREEN))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.RIGHT, Color.MAGENTA))

        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.LEFT, Color.RED))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.RIGHT, Color.YELLOW))

        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.LEFT, Color.GREEN))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.RIGHT, Color.MAGENTA))

        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.LEFT, Color.YELLOW))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.RIGHT, Color.DKGRAY))

        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.LEFT, Color.CYAN))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.NONE, Color.TRANSPARENT))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.RIGHT, Color.BLACK))

        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.BOTTOM, Color.BLUE))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.BOTTOM, Color.GRAY))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.BOTTOM, Color.GREEN))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.BOTTOM, Color.BLACK))
        listOfBoardPositions.add(BoardPosition("place 1", BoardRegion.BOTTOM, Color.BLUE))
        return listOfBoardPositions
    }

    private fun goTo(coordinate: Coordinate, number: Int) {

        if (coordinate.boardPosition?.boardRegion == BoardRegion.TOP) {
            if (imageView!![number].y == coordinate.y) {
                if (coordinate.x > imageView!![number].x) {
                    ObjectAnimator.ofFloat(imageView!![number], "translationX", coordinate.x).apply {
                        duration = 500
                        start()
                    }
                } else {
                    ObjectAnimator.ofFloat(
                        imageView!![number],
                        "translationX",
                        screenWidth.toFloat() - imageView!![number].width
                    ).apply {
                        duration = 500
                        start()
                    }.doOnEnd {
                        ObjectAnimator.ofFloat(
                            imageView!![number],
                            "translationY",
                            screenHeight.toFloat() - imageView!![number].height
                        ).apply {
                            duration = 500
                            start()
                        }.doOnEnd {
                            ObjectAnimator.ofFloat(imageView!![number], "translationX", 0f).apply {
                                duration = 500
                                start()
                            }.doOnEnd {
                                ObjectAnimator.ofFloat(
                                    imageView!![number],
                                    "translationY",
                                    coordinate.y
                                )
                                    .apply {
                                        duration = 500
                                        start()
                                    }.doOnEnd {
                                        ObjectAnimator.ofFloat(
                                            imageView!![number],
                                            "translationX",
                                            coordinate.x
                                        )
                                            .apply {
                                                duration = 500
                                                start()
                                            }
                                    }
                            }
                        }
                    }
                }
            } else if (imageView!![number].x == 0f) {
                ObjectAnimator.ofFloat(imageView!![number], "translationY", coordinate.y).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView!![number], "translationX", coordinate.x).apply {
                        duration = 500
                        start()
                    }
                }
            } else if (imageView!![number].x == screenWidth.toFloat() - coordinate.width) {
                ObjectAnimator.ofFloat(
                    imageView!![number],
                    "translationY",
                    screenHeight.toFloat() - imageView!![number].height
                ).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView!![number], "translationX", 0f).apply {
                        duration = 500
                        start()
                    }.doOnEnd {
                        ObjectAnimator.ofFloat(imageView!![number], "translationY", coordinate.y)
                            .apply {
                                duration = 500
                                start()
                            }.doOnEnd {
                                ObjectAnimator.ofFloat(
                                    imageView!![number],
                                    "translationX",
                                    coordinate.x
                                )
                                    .apply {
                                        duration = 500
                                        start()
                                    }
                            }
                    }
                }
            } else if (imageView!![number].y <= screenHeight.toFloat()) {
                ObjectAnimator.ofFloat(imageView?.get(number), "translationX", 0f).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationY", coordinate.y)
                        .apply {
                            duration = 500
                            start()
                        }.doOnEnd {
                            ObjectAnimator.ofFloat(
                                imageView?.get(number),
                                "translationX",
                                coordinate.x
                            )
                                .apply {
                                    duration = 500
                                    start()
                                }
                        }
                }
            }
        } else if (coordinate.boardPosition?.boardRegion == BoardRegion.RIGHT) {
            if (imageView!![number].x == coordinate.x) {
                if (coordinate.y > imageView!![number].y) {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationY", coordinate.y)
                        .apply {
                            duration = 500
                            start()
                        }
                } else {
                    ObjectAnimator.ofFloat(
                        imageView?.get(number),
                        "translationY",
                        screenHeight.toFloat() - imageView!![number].height
                    )
                        .apply {
                            duration = 500
                            start()
                        }.doOnEnd {
                            ObjectAnimator.ofFloat(imageView?.get(number), "translationX", 0f)
                                .apply {
                                    duration = 500
                                    start()
                                }.doOnEnd {
                                    ObjectAnimator.ofFloat(
                                        imageView?.get(number),
                                        "translationY",
                                        0f
                                    )
                                        .apply {
                                            duration = 500
                                            start()
                                        }.doOnEnd {
                                            ObjectAnimator.ofFloat(
                                                imageView?.get(number),
                                                "translationX",
                                                coordinate.x
                                            )
                                                .apply {
                                                    duration = 500
                                                    start()
                                                }.doOnEnd {
                                                    ObjectAnimator.ofFloat(
                                                        imageView?.get(number),
                                                        "translationY",
                                                        coordinate.y
                                                    )
                                                        .apply {
                                                            duration = 500
                                                            start()
                                                        }
                                                }
                                        }
                                }
                        }
                }
            } else if (imageView!![number].y == 0f) {
                ObjectAnimator.ofFloat(imageView?.get(number), "translationX", coordinate.x).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationY", coordinate.y)
                        .apply {
                            duration = 500
                            start()
                        }
                }
            } else if (imageView!![number].x == 0f) {
                ObjectAnimator.ofFloat(imageView?.get(number), "translationY", 0f).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationX", coordinate.x)
                        .apply {
                            duration = 500
                            start()
                        }.doOnEnd {
                            ObjectAnimator.ofFloat(
                                imageView?.get(number),
                                "translationY",
                                coordinate.y
                            )
                                .apply {
                                    duration = 500
                                    start()
                                }
                        }
                }
            } else if (imageView!![number].y <= screenHeight.toFloat()) {
                ObjectAnimator.ofFloat(imageView?.get(number), "translationX", 0f).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationY", 0f).apply {
                        duration = 500
                        start()
                    }.doOnEnd {
                        ObjectAnimator.ofFloat(imageView?.get(number), "translationX", coordinate.x)
                            .apply {
                                duration = 500
                                start()
                            }.doOnEnd {
                                ObjectAnimator.ofFloat(
                                    imageView?.get(number),
                                    "translationY",
                                    coordinate.y
                                )
                                    .apply {
                                        duration = 500
                                        start()
                                    }
                            }
                    }
                }
            }
        } else if (coordinate.boardPosition?.boardRegion == BoardRegion.LEFT) {
            if (imageView!![number].x == coordinate.x) {
                if (coordinate.y < imageView!![number].y) {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationY", coordinate.y)
                        .apply {
                            duration = 500
                            start()
                        }
                } else {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationY", 0f).apply {
                        duration = 500
                        start()
                    }.doOnEnd {
                        ObjectAnimator.ofFloat(
                            imageView?.get(number),
                            "translationX",
                            screenWidth.toFloat() - imageView!![number].width
                        ).apply {
                            duration = 500
                            start()
                        }.doOnEnd {
                            ObjectAnimator.ofFloat(
                                imageView?.get(number),
                                "translationY",
                                screenHeight.toFloat() - imageView!![number].height
                            ).apply {
                                duration = 500
                                start()
                            }.doOnEnd {
                                ObjectAnimator.ofFloat(
                                    imageView?.get(number),
                                    "translationX",
                                    coordinate.x
                                )
                                    .apply {
                                        duration = 500
                                        start()
                                    }.doOnEnd {
                                        ObjectAnimator.ofFloat(
                                            imageView?.get(number),
                                            "translationY",
                                            coordinate.y
                                        )
                                            .apply {
                                                duration = 500
                                                start()
                                            }
                                    }
                            }
                        }
                    }
                }
            } else if (imageView!![number].y == 0f) {
                ObjectAnimator.ofFloat(
                    imageView?.get(number),
                    "translationX",
                    screenWidth.toFloat() - imageView!![number].width
                ).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(
                        imageView?.get(number),
                        "translationY",
                        screenHeight.toFloat() - imageView!![number].height
                    ).apply {
                        duration = 500
                        start()
                    }.doOnEnd {
                        ObjectAnimator.ofFloat(imageView?.get(number), "translationX", coordinate.x)
                            .apply {
                                duration = 500
                                start()
                            }.doOnEnd {
                                ObjectAnimator.ofFloat(
                                    imageView?.get(number),
                                    "translationY",
                                    coordinate.y
                                )
                                    .apply {
                                        duration = 500
                                        start()
                                    }
                            }
                    }
                }
            } else if (imageView!![number].y == screenHeight.toFloat() - coordinate.height) {
                ObjectAnimator.ofFloat(imageView?.get(number), "translationX", coordinate.x).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationY", coordinate.y)
                        .apply {
                            duration = 500
                            start()
                        }
                }
            } else if (imageView!![number].x == screenWidth.toFloat() - coordinate.width) {
                ObjectAnimator.ofFloat(
                    imageView?.get(number),
                    "translationY",
                    screenHeight.toFloat() - imageView!![number].height
                ).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationX", coordinate.x)
                        .apply {
                            duration = 500
                            start()
                        }.doOnEnd {
                            ObjectAnimator.ofFloat(
                                imageView?.get(number),
                                "translationY",
                                coordinate.y
                            )
                                .apply {
                                    duration = 500
                                    start()
                                }
                        }
                }
            }
        } else if (coordinate.boardPosition?.boardRegion == BoardRegion.BOTTOM) {
            if (imageView!![number].y == coordinate.y) {
                if (coordinate.x < imageView!![number].x) {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationX", coordinate.x)
                        .apply {
                            duration = 500
                            start()
                        }
                } else {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationX", 0f).apply {
                        duration = 500
                        start()
                    }.doOnEnd {
                        ObjectAnimator.ofFloat(imageView?.get(number), "translationY", 0f).apply {
                            duration = 500
                            start()
                        }.doOnEnd {
                            ObjectAnimator.ofFloat(
                                imageView?.get(number),
                                "translationX",
                                screenWidth.toFloat() - imageView!![number].width
                            ).apply {
                                duration = 500
                                start()
                            }.doOnEnd {
                                ObjectAnimator.ofFloat(
                                    imageView?.get(number),
                                    "translationY",
                                    coordinate.y
                                )
                                    .apply {
                                        duration = 500
                                        start()
                                    }.doOnEnd {
                                        ObjectAnimator.ofFloat(
                                            imageView?.get(number),
                                            "translationX",
                                            coordinate.x
                                        )
                                            .apply {
                                                duration = 500
                                                start()
                                            }
                                    }
                            }
                        }
                    }
                }
            } else if (imageView!![number].y == 0f) {
                ObjectAnimator.ofFloat(
                    imageView?.get(number),
                    "translationX",
                    screenWidth.toFloat() - imageView!![number].width
                ).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationY", coordinate.y)
                        .apply {
                            duration = 500
                            start()
                        }.doOnEnd {
                            ObjectAnimator.ofFloat(
                                imageView?.get(number),
                                "translationX",
                                coordinate.x
                            )
                                .apply {
                                    duration = 500
                                    start()
                                }
                        }
                }
            } else if (imageView!![number].x == 0f) {
                ObjectAnimator.ofFloat(imageView?.get(number), "translationY", 0f).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(
                        imageView?.get(number),
                        "translationX",
                        screenWidth.toFloat() - imageView!![number].width
                    ).apply {
                        duration = 500
                        start()
                    }.doOnEnd {
                        ObjectAnimator.ofFloat(imageView?.get(number), "translationY", coordinate.y)
                            .apply {
                                duration = 500
                                start()
                            }.doOnEnd {
                                ObjectAnimator.ofFloat(
                                    imageView?.get(number),
                                    "translationX",
                                    coordinate.x
                                )
                                    .apply {
                                        duration = 500
                                        start()
                                    }
                            }
                    }
                }
            } else if (imageView!![number].x == screenWidth.toFloat() - coordinate.width) {
                ObjectAnimator.ofFloat(imageView?.get(number), "translationY", coordinate.y).apply {
                    duration = 500
                    start()
                }.doOnEnd {
                    ObjectAnimator.ofFloat(imageView?.get(number), "translationX", coordinate.x)
                        .apply {
                            duration = 500
                            start()
                        }
                }
            }
        } else {
            ObjectAnimator.ofFloat(imageView?.get(number), "translationX", coordinate.x).apply {
                duration = 500
                start()
            }.doOnEnd {
                ObjectAnimator.ofFloat(imageView?.get(number), "translationY", coordinate.y).apply {
                    duration = 500
                    start()
                }
            }
        }
    }

    override fun updatePosition(
        width: Int,
        height: Int,
        x: Float,
        y: Float,
        boardPosition: BoardPosition
    ) {
        if (player.coordinates?.x != x
            || player.coordinates?.y != y
        ) {
            val newCoordinates = Coordinate(width, height, x, y, boardPosition)
            updateCoordinates(newCoordinates)
        }
    }

    private fun updateCoordinates(newCoordinates: Coordinate) {
        Util.db.child(Constants.DATABASE_REF_MATCH).child(match.uid)
            .child(Constants.DATABASE_REF_PLAYER).child(player.number.toString())
            .child(Constants.DATABASE_REF_COORDINATE).setValue(newCoordinates)
    }
}