from web3 import Web3

# -------- CONFIG --------
RPC_URL = "https://ethereum.publicnode.com"
CONTRACT = "0x44203E9DdBd65a544F4abA5372F7D4a0cDcDE2aC"

w3 = Web3(Web3.HTTPProvider(RPC_URL))
assert w3.is_connected(), "RPC connection failed"

# -------- COMPUTE STORAGE SLOT FOR MAPPING --------
key = b"4n4lyz1ng_byt3c0d3_l1k3_4_pr0"
slot_index = (0).to_bytes(32, "big")  # usually 0 if first variable

slot = Web3.keccak(key + slot_index)

print("Computed storage slot:", slot.hex())

# -------- READ STORAGE --------
value = w3.eth.get_storage_at(CONTRACT, slot)
print("Raw hex value:", value.hex())

int_value = int.from_bytes(value, "big")
print("Decimal (wei):", int_value)

eth_value = w3.from_wei(int_value, "ether")
print("ETH:", eth_value)
