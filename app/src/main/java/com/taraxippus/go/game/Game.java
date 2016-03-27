package com.taraxippus.go.game;

import android.opengl.*;
import com.taraxippus.go.*;
import com.taraxippus.go.game.gameobject.*;
import com.taraxippus.go.render.*;
import com.taraxippus.go.util.*;

public class Game
{
	public final Main main;

	public final VectorF light = new VectorF();
	public final VectorF light2 = new VectorF(-0.357F, -1, 0.489F).normalize();
	
	public SceneObject boardObject;
	
	public final byte[] board = new byte[getWidth() * getLength()];
	public final boolean[] board_checked = new boolean[getWidth() * getLength()];
	public final GoStone[] board_stones = new GoStone[getWidth() * getLength()];
	
	public final byte[] board_prev = new byte[getWidth() * getLength()];
	public final byte[] board_tmp = new byte[getWidth() * getLength()];
	
	public final GoStone[] blackStones = new GoStone[getWidth() * 2];
	public final GoStone[] whiteStones = new GoStone[getWidth() * 2];
	
	public int black_captured = 0;
	public int white_captured = 0;
	
	public Game(Main main)
	{
		this.main = main;
	}
	
	public void init()
	{
		light.set(getWidth() / 2F - 0.5F, getHeight() / 2F - 0.5F, getLength() / 2F - 0.5F);
		main.camera.position.set(getWidth() / 2F - 0.5F, 0, getLength() / 2F - 0.5F);
		main.camera.init();
		
		main.world.add(new Skybox(main.world));
		
		main.world.add(boardObject = (SceneObject) new BoxTop(main.world).scale(getWidth(), 0.5F, getLength()).translate(getWidth() / 2F - 0.5F, -0.75F, getLength() / 2F - 0.5F).setHasReflection(false).setPass(Pass.SCENE_MIRROR));
		main.world.add(new BoxBottom(main.world).scale(getWidth(), 0.5F, getLength()).translate(getWidth() / 2F - 0.5F, -0.75F, getLength() / 2F - 0.5F).setHasReflection(false).setColor(0x333333));
		
		main.world.add(new BoxTop(main.world).scale(getWidth(), 0.5F, 4).translate(getWidth() / 2F - 0.5F, -0.75F, -3F - 0.5F).setHasReflection(false).setColor(0xDDDDDD).setPass(Pass.SCENE_MIRROR));
		main.world.add(new BoxBottom(main.world).scale(getWidth(), 0.5F, 4).translate(getWidth() / 2F - 0.5F, -0.75F, -3F - 0.5F).setHasReflection(false).setColor(0x333333));
		
		main.world.add(new BoxTop(main.world).scale(getWidth(), 0.5F, 4).translate(getWidth() / 2F - 0.5F, -0.75F, getLength() + 3F - 0.5F).setHasReflection(false).setColor(0x444444).setPass(Pass.SCENE_MIRROR));
		main.world.add(new BoxBottom(main.world).scale(getWidth(), 0.5F, 4).translate(getWidth() / 2F - 0.5F, -0.75F, getLength() + 3F - 0.5F).setHasReflection(false).setColor(0x333333));
		
		for (int i = 0; i < blackStones.length; ++i)
			main.world.add(blackStones[i] = ((GoStone) new GoStone(main.world, true).translate(i % getWidth(), 0, getLength() + 1 + (i / getWidth()))).setPositionWithDelay(i % getWidth(), getLength() + 1 + (i / getWidth()), -1));

		for (int i = 0; i < whiteStones.length; ++i)
			main.world.add(whiteStones[i] = ((GoStone) new GoStone(main.world, false).translate(i % getWidth(), 0, - 2 - (i / getWidth()))).setPositionWithDelay(i % getWidth(), - 2 - (i / getWidth()), -1));
		
		
		main.world.add(new Grid(main.world, new VectorF(getWidth(), getHeight(), getLength())).translate(getWidth() / 2F - 0.5F, getHeight() / 2F - 0.5F, getLength() / 2F - 0.5F).setHasReflection(false).setColor(0x555555));
		main.world.add(new FullscreenQuad(main.world, Pass.POST));
	}
	
	public void update()
	{
		main.world.update();
	}
	
	public void updateReal()
	{
		main.camera.update();
	}
	
	boolean black = true;
	
	public void onWallTouched(VectorF intersection, VectorF normal)
	{
		intersection.roundInt();
		
		if (get((int) intersection.x, (int) intersection.z) == 0)
		{
			GoStone move = null;
			boolean reserve = false;
			for (GoStone stone : black ? blackStones : whiteStones)
				if (black ? stone.z > getLength() : stone.z < 0)
				{
					if (move == null)
						move = stone;
					else
					{
						reserve = true;
						break;
					}
				}
			
			if (move == null)
				return;
				
			System.arraycopy(board, 0, board_tmp, 0, board.length);
			set((int) intersection.x, (int) intersection.z, black ? (byte) 1 : (byte) 2);
				
			boolean captureXP = false, captureXN = false, captureZP = false, captureZN = false;
				
			if (get((int) intersection.x - 1, (int) intersection.z) == (black ? (byte) 2 : (byte) 1) && !hasLiberties((int) intersection.x - 1, (int) intersection.z, black ? (byte) 2 : (byte) 1))
			{
				capturePre((int) intersection.x - 1, (int) intersection.z, black ? (byte) 2 : (byte) 1, 1);
				captureXN = true;
			}
				
			if (get((int) intersection.x + 1, (int) intersection.z) == (black ? (byte) 2 : (byte) 1) && !hasLiberties((int) intersection.x + 1, (int) intersection.z, black ? (byte) 2 : (byte) 1))
			{
				capturePre((int) intersection.x + 1, (int) intersection.z, black ? (byte) 2 : (byte) 1, 1);
				captureXP = true;
			}
				
			if (get((int) intersection.x, (int) intersection.z - 1) == (black ? (byte) 2 : (byte) 1) && !hasLiberties((int) intersection.x, (int) intersection.z - 1, black ? (byte) 2 : (byte) 1))
			{
				capturePre((int) intersection.x, (int) intersection.z - 1, black ? (byte) 2 : (byte) 1, 1);
				captureZN = true;
			}
				
			if (get((int) intersection.x, (int) intersection.z + 1) == (black ? (byte) 2 : (byte) 1) && !hasLiberties((int) intersection.x, (int) intersection.z + 1, black ? (byte) 2 : (byte) 1))
			{
				capturePre((int) intersection.x, (int) intersection.z + 1, black ? (byte) 2 : (byte) 1, 1);
				captureZP = true;
			}
				
				
			if (hasLiberties((int)intersection.x, (int)intersection.z, black ? (byte) 1 : (byte) 2) && !isKo())
			{
				System.arraycopy(board_tmp, 0, board, 0, board.length);
				set((int) intersection.x, (int) intersection.z, black ? (byte) 1 : (byte) 2);
				
				move.setPosition((int) intersection.x, (int) intersection.z);
				board_stones[(int) intersection.x * getLength() + (int) intersection.z] = move;

				if (captureXN)
					capture((int) intersection.x - 1, (int) intersection.z, black ? (byte) 2 : (byte) 1, 1);
				
				if (captureXP)
					capture((int) intersection.x + 1, (int) intersection.z, black ? (byte) 2 : (byte) 1, 1);
				
				if (captureZN)
					capture((int) intersection.x, (int) intersection.z - 1, black ? (byte) 2 : (byte) 1, 1);
				
				if (captureZP)
					capture((int) intersection.x, (int) intersection.z + 1, black ? (byte) 2 : (byte) 1, 1);
				
				if (!reserve)
				{
					if (black)
					{
						for (int i = 0; i < blackStones.length; ++i)
							main.world.addLater(blackStones[i] = ((GoStone) new GoStone(main.world, true).translate(i % getWidth(), 0, getLength() + 1 + (i / getWidth()))).setPositionWithDelay(i % getWidth(), getLength() + 1 + (i / getWidth()), -2));

					}
					else
					{
						for (int i = 0; i < whiteStones.length; ++i)
							main.world.addLater(whiteStones[i] = ((GoStone) new GoStone(main.world, false).translate(i % getWidth(), 0, - 2 - (i / getWidth()))).setPositionWithDelay(i % getWidth(), - 2 - (i / getWidth()), -2));

					}

				}
				
				System.arraycopy(board_tmp, 0, board_prev, 0, board.length);
				
				black = !black;
				main.runOnUiThread(new Runnable()
				{
						@Override
						public void run()
						{
							main.blackView.setTextColor(black ? 0xFFFFFFFF : 0x88FFFFFF);
							main.whiteView.setTextColor(!black ? 0xFFFFFFFF : 0x88FFFFFF);
						}
				});
			}
			else
			{
				System.arraycopy(board_tmp, 0, board, 0, board.length);
			}
				
		}
	}
	
	public void delete()
	{
		main.world.delete();
	}
	
	public int getWidth()
	{
		return 19;
	}
	
	public int getHeight()
	{
		return 19;
	}
	
	public int getLength()
	{
		return 19;
	}
	
	public byte get(int x, int z)
	{
		if (x < 0 || x >= getWidth() || z < 0 || z >= getLength())
			return (byte) 3;
			
		return board[x * getLength() + z];
	}
	
	public void set(int x, int z, byte set)
	{
		if (x < 0 || x >= getWidth() || z < 0 || z >= getLength())
			return;
		
		board[x * getLength() + z] = set;
	}
	
	public void capturePre(int x, int z, byte color, int recursiv)
	{
		if (x < 0 || x >= getWidth() || z < 0 || z >= getLength() || get(x, z) != color)
			return;

		set(x, z, (byte) 0);
		
		if (get(x - 1, z) == color)
			capturePre(x - 1, z, color, recursiv + 1);

		if (get(x + 1, z) == color)
			capturePre(x + 1, z, color, recursiv + 1);

		if (get(x, z - 1) == color)
			capturePre(x, z - 1, color, recursiv + 1);

		if (get(x, z + 1) == color)
			capturePre(x, z + 1, color, recursiv + 1);
	}
		
	public void capture(int x, int z, byte color, int recursiv)
	{
		if (x < 0 || x >= getWidth() || z < 0 || z >= getLength() || get(x, z) != color)
			return;
		
		set(x, z, (byte) 0);
		
		board_stones[x * getLength() + z].setPositionWithDelay(color == 1 ? black_captured % getLength() : white_captured % getLength(), color == 2 ? getLength() + 3 + white_captured / getLength() : -4 - black_captured / getLength(), recursiv);
		board_stones[x * getLength() + z] = null;
		
		if (color == 1)
			black_captured++;
		else
			white_captured++;
		
		if (get(x - 1, z) == color)
			capture(x - 1, z, color, recursiv + 1);
			
		if (get(x + 1, z) == color)
			capture(x + 1, z, color, recursiv + 1);
			
		if (get(x, z - 1) == color)
			capture(x, z - 1, color, recursiv + 1);
			
		if (get(x, z + 1) == color)
			capture(x, z + 1, color, recursiv + 1);
	}
	
	public boolean hasLiberties(int x, int z, byte color)
	{
		clearCheck();
	
		return hasLiberties_recursiv(x, z, color);
	}
	
	private boolean hasLiberties_recursiv(int x, int z, byte color)
	{
		check(x, z);
		
		if (x < 0 || x >= getWidth() || z < 0 || z >= getLength())
			return false;

		if (!checked(x - 1, z) && get(x - 1, z) == 0)
			return true;
		else if (!checked(x - 1, z) && get(x - 1, z) == color && hasLiberties_recursiv(x - 1, z, color))
			return true;
			
		else if (!checked(x + 1, z) && get(x + 1, z) == 0)
			return true;
		else if (!checked(x + 1, z) && get(x + 1, z) == color && hasLiberties_recursiv(x + 1, z, color))
			return true;
		
		else if (!checked(x, z - 1) && get(x, z - 1) == 0)
			return true;
		else if (!checked(x, z - 1) && get(x, z - 1) == color && hasLiberties_recursiv(x, z - 1, color))
			return true;
		
		else if (!checked(x, z + 1) && get(x, z + 1) == 0)
			return true;
		else if (!checked(x, z + 1) && get(x, z + 1) == color && hasLiberties_recursiv(x, z + 1, color))
			return true;
			
		return false;
	}
	
	public void clearCheck()
	{
		for (int i = 0; i < board_checked.length; ++i)
			board_checked[i] = false;
	}
	
	public void check(int x, int z)
	{
		if (x < 0 || x >= getWidth() || z < 0 || z >= getLength())
			return;

		board_checked[x * getLength() + z] = true;
	}
	
	public boolean checked(int x, int z)
	{
		if (x < 0 || x >= getWidth() || z < 0 || z >= getLength())
			return true;

		return board_checked[x * getLength() + z];
	}
	
	public boolean isKo()
	{
		for (int i = 0; i < board.length; ++i)
			if (board[i] != board_prev[i])
				return false;
		
		return true;
	}
}
