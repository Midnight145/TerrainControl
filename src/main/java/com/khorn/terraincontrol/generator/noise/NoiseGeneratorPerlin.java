package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorPerlin {
	private final short[] permutations;
	public final double xCoord;
	public final double yCoord;
	public final double zCoord;

	// Gradient vectors for Perlin noise. Each entry is the coefficients for (x, y, z).
	private static final double[] GRAD_X = {1,-1, 1,-1, 1,-1, 1,-1, 0, 0, 0, 0, 1, 0,-1, 0};
	private static final double[] GRAD_Y = {1, 1,-1,-1, 0, 0, 0, 0, 1,-1, 1,-1, 1,-1, 1,-1};
	private static final double[] GRAD_Z = {0, 0, 0, 0, 1, 1,-1,-1, 1, 1,-1,-1, 0, 1, 0,-1};

	public NoiseGeneratorPerlin(Random random) {
		permutations = new short[512];
		xCoord = random.nextDouble() * 256D;
		yCoord = random.nextDouble() * 256D;
		zCoord = random.nextDouble() * 256D;
		for (int i = 0; i < 256; i++) {
			permutations[i] = (short) i;
		}

		for (int j = 0; j < 256; j++) {
			int k = random.nextInt(256 - j) + j;
			short l = permutations[j];
			permutations[j] = permutations[k];
			permutations[k] = l;
			permutations[j + 256] = permutations[j];
		}
	}

	private static double lerp(double d, double d1, double d2) {
		return d1 + d * (d2 - d1);
	}

	public final double func_4110_a(int i, double d, double d1) {
		int j = i & 0xf;
		double d2 = (double) (1 - ((j & 8) >> 3)) * d;
		double d3 = j >= 4 ? j != 12 && j != 14 ? d1 : d : 0.0D;
		return ((j & 1) != 0 ? -d2 : d2) + ((j & 2) != 0 ? -d3 : d3);
	}

	public final double grad(int i, double d, double d1, double d2) {
		int h = i & 0xf;
		return GRAD_X[h] * d + GRAD_Y[h] * d1 + GRAD_Z[h] * d2;
	}

	public void populateNoiseArray3D(double[] NoiseArray, double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize,
			double xScale, double yScale, double zScale, double noiseScale) {
		final short[] perm = this.permutations;
		int i1 = 0;
		double d7 = 1.0D / noiseScale;
		double d13 = 0.0D;
		double d15 = 0.0D;
		double d16 = 0.0D;
		double d18 = 0.0D;
		for (int i5 = 0; i5 < xSize; i5++) {
			double d20 = xOffset + (double) i5 * xScale + xCoord;
			int k5 = (int) d20;
			if (d20 < (double) k5) { k5--; }
			int i6 = k5 & 0xff;
			d20 -= k5;
			double d22 = d20 * d20 * d20 * (d20 * (d20 * 6D - 15D) + 10D);
			int perm_i6 = perm[i6];
			int perm_i6p1 = perm[i6 + 1];
			double d20m1 = d20 - 1.0D;
			for (int j6 = 0; j6 < zSize; j6++) {
				double d24 = zOffset + (double) j6 * zScale + zCoord;
				int k6 = (int) d24;
				if (d24 < (double) k6) { k6--; }
				int l6 = k6 & 0xff;
				d24 -= k6;
				double d25 = d24 * d24 * d24 * (d24 * (d24 * 6D - 15D) + 10D);
				double d24m1 = d24 - 1.0D;
				int i7 = 0;
				while (i7 < ySize) {
					double d26 = yOffset + (double) i7 * yScale + yCoord;
					int j7 = (int) d26;
					if (d26 < (double) j7) { j7--; }
					int k7 = j7 & 0xff;
					d26 -= j7;
					double d27 = d26 * d26 * d26 * (d26 * (d26 * 6D - 15D) + 10D);
					double d26m1 = d26 - 1.0D;
					int j2 = perm_i6 + k7;
					int k2 = perm[j2] + l6;
					int l2 = perm[j2 + 1] + l6;
					int i3 = perm_i6p1 + k7;
					int k3 = perm[i3] + l6;
					int l3 = perm[i3 + 1] + l6;
					d13 = lerp(d22, grad(perm[k2], d20, d26, d24), grad(perm[k3], d20m1, d26, d24));
					d15 = lerp(d22, grad(perm[l2], d20, d26m1, d24), grad(perm[l3], d20m1, d26m1, d24));
					d16 = lerp(d22, grad(perm[k2 + 1], d20, d26, d24m1),
							grad(perm[k3 + 1], d20m1, d26, d24m1));
					d18 = lerp(d22, grad(perm[l2 + 1], d20, d26m1, d24m1),
							grad(perm[l3 + 1], d20m1, d26m1, d24m1));
					// First sample in this cell
					double d28 = lerp(d27, d13, d15);
					double d29 = lerp(d27, d16, d18);
					double d30 = lerp(d25, d28, d29);
					NoiseArray[i1++] += d30 * d7;
					i7++;
					while (i7 < ySize) {
						d26 = yOffset + (double) i7 * yScale + yCoord;
						j7 = (int) d26;
						if (d26 < (double) j7) { j7--; }
						if ((j7 & 0xff) != k7) break;
						d26 -= j7;
						d27 = d26 * d26 * d26 * (d26 * (d26 * 6D - 15D) + 10D);
						d28 = lerp(d27, d13, d15);
						d29 = lerp(d27, d16, d18);
						d30 = lerp(d25, d28, d29);
						NoiseArray[i1++] += d30 * d7;
						i7++;
					}
				}
			}
		}
	}

	public void populateNoiseArray2D(double[] NoiseArray, double xOffset, double zOffset, int xSize, int zSize, double xScale,
			double zScale, double noiseScale) {
		final short[] perm = this.permutations;
		int j3 = 0;
		double d12 = 1.0D / noiseScale;
		for (int i4 = 0; i4 < xSize; i4++) {
			double d14 = xOffset + (double) i4 * xScale + xCoord;
			int j4 = (int) d14;
			if (d14 < (double) j4) { j4--; }
			int k4 = j4 & 0xff;
			d14 -= j4;
			double d17 = d14 * d14 * d14 * (d14 * (d14 * 6D - 15D) + 10D);
			int perm_k4 = perm[k4];
			int perm_k4p1 = perm[k4 + 1];
			for (int l4 = 0; l4 < zSize; l4++) {
				double d19 = zOffset + (double) l4 * zScale + zCoord;
				int j5 = (int) d19;
				if (d19 < (double) j5) { j5--; }
				int l5 = j5 & 0xff;
				d19 -= j5;
				double d21 = d19 * d19 * d19 * (d19 * (d19 * 6D - 15D) + 10D);
				int j1 = perm[perm_k4] + l5;
				int l1 = perm[perm_k4p1] + l5;
				double d9 = lerp(d17, func_4110_a(perm[j1], d14, d19), grad(perm[l1], d14 - 1.0D, 0.0D, d19));
				double d11 = lerp(d17, grad(perm[j1 + 1], d14, 0.0D, d19 - 1.0D),
						grad(perm[l1 + 1], d14 - 1.0D, 0.0D, d19 - 1.0D));
				double d23 = lerp(d21, d9, d11);
				NoiseArray[j3++] += d23 * d12;
			}
		}

	}
}
